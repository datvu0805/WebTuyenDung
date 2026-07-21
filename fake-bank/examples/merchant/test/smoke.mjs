import assert from 'node:assert/strict';
import { createServer } from 'node:http';
import { test } from 'node:test';
import { createMerchantApp } from '../src/server.mjs';

test('example merchant creates payment and records webhook result', () => {
  const provider = createProviderStub();
  let merchant;

  return provider.listen()
    .then(() => {
      merchant = createMerchantApp({
        host: '127.0.0.1',
        port: 0,
        fakeBankBaseUrl: provider.baseUrl(),
        publicBaseUrl: 'http://127.0.0.1:8090',
      });

      return merchant.listen();
    })
    .then(() => {
      const merchantBaseUrl = getServerBaseUrl(merchant.server);

      return fetch(`${merchantBaseUrl}/payments`, {
        method: 'POST',
        headers: {
          'content-type': 'application/x-www-form-urlencoded',
        },
        body: new URLSearchParams({
          amount: '199.99',
          currency: 'EUR',
          scenario: 'card_success',
        }),
        redirect: 'manual',
      })
        .then((createResponse) => {
          assert.equal(createResponse.status, 303);
          assert.equal(provider.requests.length, 1);

          const location = createResponse.headers.get('location');
          assert.equal(location, '/payments/pay_example');

          return fetch(`${merchantBaseUrl}${location}`).then((response) => {
            assert.equal(response.status, 200);
            return response.text();
          });
        })
        .then((paymentPage) => {
          assert.match(paymentPage, /Payment Result/);
          assert.match(paymentPage, /Open checkout/);
          assert.match(paymentPage, /199.99 EUR/);
          assert.match(paymentPage, /card_success/);
          assert.match(paymentPage, /http:\/\/checkout.example\/checkout\/pay_example/);

          return fetch(`${merchantBaseUrl}/webhooks/fake-bank`, {
            method: 'POST',
            headers: {
              'content-type': 'application/json',
            },
            body: JSON.stringify({
              event: 'payment.succeeded',
              event_id: 'event_example',
              payment_id: 'pay_example',
              merchant_id: 'example-merchant',
              merchant_reference: 'demo_order_123',
              amount: 199.99,
              currency: 'EUR',
              created_at: new Date().toISOString(),
            }),
          });
        })
        .then((webhookResponse) => {
          assert.equal(webhookResponse.status, 200);

          return fetch(`${merchantBaseUrl}/payments/pay_example`).then((response) => {
            assert.equal(response.status, 200);
            return response.text();
          });
        })
        .then((finalPage) => {
          assert.match(finalPage, /Succeeded/);
          assert.match(finalPage, /payment\.succeeded/);
        });
    })
    .finally(() => {
      const closeMerchant = merchant ? merchant.close() : Promise.resolve();
      return closeMerchant.then(() => provider.close());
    });
});

function createProviderStub() {
  const requests = [];
  const server = createServer((request, response) => {
    if (request.method === 'POST' && request.url === '/v1/payments') {
      readJson(request).then((body) => {
        requests.push(body);
        response.writeHead(201, { 'content-type': 'application/json' });
        response.end(JSON.stringify({
          payment_id: 'pay_example',
          status: 'created',
          checkout_url: 'http://checkout.example/checkout/pay_example',
        }));
      });
      return;
    }

    response.writeHead(404);
    response.end();
  });

  return {
    requests,
    listen() {
      return new Promise((resolve) => server.listen(0, '127.0.0.1', resolve));
    },
    close() {
      return new Promise((resolve, reject) => {
        server.close((error) => {
          if (error) {
            reject(error);
            return;
          }

          resolve();
        });
      });
    },
    baseUrl() {
      return getServerBaseUrl(server);
    },
  };
}

function getServerBaseUrl(server) {
  const address = server.address();
  assert(address && typeof address === 'object');
  return `http://127.0.0.1:${address.port}`;
}

function readJson(request) {
  return new Promise((resolve, reject) => {
    const chunks = [];
    request.on('data', (chunk) => chunks.push(chunk));
    request.on('end', () => resolve(JSON.parse(Buffer.concat(chunks).toString('utf8'))));
    request.on('error', reject);
  });
}
