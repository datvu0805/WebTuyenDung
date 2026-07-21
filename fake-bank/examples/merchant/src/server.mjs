import { createServer } from 'node:http';
import { randomUUID } from 'node:crypto';

const defaultConfig = {
  host: process.env.MERCHANT_HOST ?? '127.0.0.1',
  port: Number(process.env.MERCHANT_PORT ?? '8090'),
  fakeBankBaseUrl: process.env.FAKE_BANK_BASE_URL ?? 'http://127.0.0.1:8080',
  publicBaseUrl: process.env.MERCHANT_PUBLIC_BASE_URL ?? 'http://127.0.0.1:8090',
};

export function createMerchantApp(config = defaultConfig) {
  const payments = new Map();

  const server = createServer((request, response) => {
    routeRequest({ request, response, payments, config }).catch((error) => {
      sendHtml(response, 500, page('Error', `<p>${escapeHtml(error.message)}</p>`));
    });
  });

  return {
    server,
    payments,
    listen() {
      return new Promise((resolve) => {
        server.listen(config.port, config.host, () => resolve(server));
      });
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
  };
}

function routeRequest({ request, response, payments, config }) {
  const url = new URL(request.url ?? '/', config.publicBaseUrl);

  if (request.method === 'GET' && url.pathname === '/') {
    sendHtml(response, 200, renderHome(payments));
    return Promise.resolve();
  }

  if (request.method === 'POST' && url.pathname === '/payments') {
    return readForm(request)
      .then((form) => createPayment(config, form)
        .then((payment) => {
          payments.set(payment.payment_id, {
            payment_id: payment.payment_id,
            status: payment.status,
            checkout_url: payment.checkout_url,
            amount: form.get('amount') || '199.99',
            currency: form.get('currency') || 'EUR',
            scenario: form.get('scenario') || 'card_success',
            webhook_event: null,
            created_at: new Date().toISOString(),
          });

          redirect(response, `/payments/${payment.payment_id}`);
        }));
  }

  if (request.method === 'GET' && url.pathname.startsWith('/payments/')) {
    const paymentId = url.pathname.split('/')[2];
    const payment = payments.get(paymentId);

    if (!payment) {
      sendHtml(response, 404, page('Payment not found', '<p>Payment was not found.</p>'));
      return Promise.resolve();
    }

    sendHtml(response, 200, renderPayment(payment));
    return Promise.resolve();
  }

  if (request.method === 'POST' && url.pathname === '/webhooks/fake-bank') {
    return readJson(request).then((payload) => {
      const existing = payments.get(payload.payment_id);

      if (existing) {
        payments.set(payload.payment_id, {
          ...existing,
          status: payload.event === 'payment.succeeded' ? 'succeeded' : 'failed',
          webhook_event: payload.event,
        });
      }

      sendJson(response, 200, { received: true });
    });
  }

  sendHtml(response, 404, page('Not found', '<p>Route was not found.</p>'));
  return Promise.resolve();
}

function createPayment(config, form) {
  const scenario = form.get('scenario') || 'card_success';
  const amount = Number(form.get('amount') || '199.99');
  const currency = form.get('currency') || 'EUR';
  const merchantReference = `demo_order_${randomUUID().replaceAll('-', '').slice(0, 8)}`;

  return fetch(`${config.fakeBankBaseUrl}/v1/payments`, {
    method: 'POST',
    headers: {
      'content-type': 'application/json',
    },
    body: JSON.stringify({
      merchant_id: 'example-merchant',
      merchant_reference: merchantReference,
      amount,
      currency,
      payment_method: 'card',
      return_url: `${config.publicBaseUrl}/`,
      webhook_url: `${config.publicBaseUrl}/webhooks/fake-bank`,
      scenario,
    }),
  }).then((response) => {
    if (!response.ok) {
      throw new Error(`Payment creation failed with HTTP ${response.status}`);
    }

    return response.json();
  });
}

function renderHome(payments) {
  const paymentList = [...payments.values()].reverse();
  const latestPayment = paymentList[0] ?? null;

  return page('Example Merchant', `
    <header class="hero">
      <div>
        <p class="eyebrow">Reference integration</p>
        <h1>Example Merchant</h1>
        <p class="subtitle">Demo merchant for testing payment flows</p>
      </div>
      <ol class="flow-steps" aria-label="Payment flow">
        <li><span>1</span>Create Payment</li>
        <li><span>2</span>Checkout</li>
        <li><span>3</span>Payment Result</li>
        <li><span>4</span>Webhook Result</li>
      </ol>
    </header>

    <section class="grid-layout" aria-label="Merchant demo workspace">
      <article class="card create-card">
        <div class="section-heading">
          <p class="eyebrow">Create Payment</p>
          <h2>Start a provider checkout</h2>
        </div>
        <form method="post" action="/payments">
          <label>
            <span>Amount</span>
            <input name="amount" value="199.99" inputmode="decimal">
          </label>
          <label>
            <span>Currency</span>
            <input name="currency" value="EUR" maxlength="3">
          </label>
          <label>
            <span>Scenario</span>
            <select name="scenario">
              <option value="card_success">Successful payment</option>
              <option value="card_declined">Failed payment</option>
            </select>
          </label>
          <button type="submit">Create payment</button>
        </form>
      </article>

      ${renderLatestPayment(latestPayment)}
    </section>

    <section class="card table-card" aria-labelledby="recent-payments-title">
      <div class="section-heading">
        <p class="eyebrow">Webhook Result</p>
        <h2 id="recent-payments-title">Recent payments</h2>
      </div>
      ${renderPaymentsTable(paymentList)}
    </section>
  `);
}

function renderPayment(payment) {
  return page('Payment Result', `
    <header class="hero">
      <div>
        <p class="eyebrow">Payment Result</p>
        <h1>${escapeHtml(payment.payment_id)}</h1>
        <p class="subtitle">Current merchant-side view of the provider payment.</p>
      </div>
      ${statusBadge(payment.status)}
    </header>

    <section class="grid-layout">
      <article class="card">
        <div class="section-heading">
          <p class="eyebrow">Payment</p>
          <h2>Summary</h2>
        </div>
        ${renderPaymentDetails(payment)}
        <div class="actions">
          ${checkoutAction(payment, 'primary')}
          <a class="button button-secondary" href="/">Create another payment</a>
        </div>
      </article>

      <article class="card">
        <div class="section-heading">
          <p class="eyebrow">Webhook Result</p>
          <h2>Merchant state</h2>
        </div>
        <dl class="details">
          <div>
            <dt>Status</dt>
            <dd>${statusBadge(payment.status)}</dd>
          </div>
          <div>
            <dt>Webhook event</dt>
            <dd>${escapeHtml(payment.webhook_event ?? 'Not received')}</dd>
          </div>
        </dl>
      </article>
    </section>
  `);
}

function renderLatestPayment(payment) {
  if (!payment) {
    return `<article class="card latest-card empty-state">
      <div class="section-heading">
        <p class="eyebrow">Latest payment</p>
        <h2>No payment created yet</h2>
      </div>
      <p>Create a payment to receive a hosted checkout URL and watch the webhook result update here.</p>
    </article>`;
  }

  return `<article class="card latest-card">
    <div class="section-heading">
      <p class="eyebrow">Latest payment</p>
      <h2>${escapeHtml(payment.payment_id)}</h2>
    </div>
    ${renderPaymentDetails(payment)}
    <div class="actions">
      ${checkoutAction(payment, 'primary')}
      <a class="button button-secondary" href="/payments/${escapeHtml(payment.payment_id)}">View result</a>
    </div>
  </article>`;
}

function renderPaymentsTable(payments) {
  if (payments.length === 0) {
    return `<div class="empty-table">No payments yet.</div>`;
  }

  const rows = payments
    .map((payment) => `<tr>
      <td><a href="/payments/${escapeHtml(payment.payment_id)}">${escapeHtml(payment.payment_id)}</a></td>
      <td>${escapeHtml(formatAmount(payment))}</td>
      <td>${statusBadge(payment.status)}</td>
      <td>${escapeHtml(formatScenario(payment.scenario))}</td>
      <td>${escapeHtml(formatDateTime(payment.created_at))}</td>
      <td>${checkoutAction(payment, 'link')}</td>
    </tr>`)
    .join('');

  return `<div class="table-wrap">
    <table>
      <thead>
        <tr>
          <th>Payment</th>
          <th>Amount</th>
          <th>Status</th>
          <th>Scenario</th>
          <th>Created</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>${rows}</tbody>
    </table>
  </div>`;
}

function renderPaymentDetails(payment) {
  return `<dl class="details">
    <div>
      <dt>Payment ID</dt>
      <dd>${escapeHtml(payment.payment_id)}</dd>
    </div>
    <div>
      <dt>Amount</dt>
      <dd>${escapeHtml(formatAmount(payment))}</dd>
    </div>
    <div>
      <dt>Status</dt>
      <dd>${statusBadge(payment.status)}</dd>
    </div>
    <div>
      <dt>Scenario</dt>
      <dd><code>${escapeHtml(payment.scenario)}</code></dd>
    </div>
  </dl>`;
}

function checkoutAction(payment, variant) {
  if (!payment.checkout_url) {
    return '';
  }

  const className = variant === 'primary' ? 'button button-primary' : 'action-link';
  return `<a class="${className}" href="${escapeHtml(payment.checkout_url)}">Open checkout</a>`;
}

function page(title, content) {
  return `<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${escapeHtml(title)}</title>
    <style>
      :root {
        color: #202124;
        background: #f5f7fb;
        font-family: Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
        font-synthesis: none;
      }

      * { box-sizing: border-box; }

      body { margin: 0; min-width: 320px; min-height: 100vh; }

      a { color: inherit; }

      .page-shell {
        width: min(1120px, 100%);
        margin: 0 auto;
        padding: 40px 20px;
      }

      .hero {
        display: flex;
        align-items: end;
        justify-content: space-between;
        gap: 24px;
        margin-bottom: 24px;
      }

      h1, h2, p { margin-top: 0; }

      h1 { margin-bottom: 8px; font-size: 34px; line-height: 1.15; }

      h2 { margin-bottom: 0; font-size: 18px; line-height: 1.3; }

      .subtitle { margin-bottom: 0; color: #5f6b7a; font-size: 16px; }

      .eyebrow {
        margin-bottom: 8px;
        color: #637083;
        font-size: 12px;
        font-weight: 800;
        letter-spacing: 0;
        text-transform: uppercase;
      }

      .flow-steps {
        display: grid;
        grid-auto-flow: column;
        gap: 22px;
        margin: 0;
        padding: 0;
        list-style: none;
        color: #526071;
        font-size: 13px;
        font-weight: 800;
      }

      .flow-steps li {
        position: relative;
        display: inline-flex;
        align-items: center;
        gap: 8px;
        white-space: nowrap;
      }

      .flow-steps li:not(:last-child)::after {
        position: absolute;
        top: 50%;
        left: calc(100% + 8px);
        width: 8px;
        height: 1px;
        background: #aeb7c4;
        content: "";
      }

      .flow-steps span {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        width: 22px;
        height: 22px;
        border: 1px solid #cfd7e3;
        border-radius: 999px;
        color: #2457a6;
        background: #eef4ff;
        font-size: 12px;
      }

      .grid-layout {
        display: grid;
        grid-template-columns: minmax(0, 0.95fr) minmax(0, 1.05fr);
        gap: 20px;
        margin-bottom: 20px;
      }

      .card {
        border: 1px solid #d8dee8;
        border-radius: 8px;
        background: #ffffff;
        padding: 24px;
        box-shadow: 0 16px 42px rgb(31 41 55 / 9%);
      }

      .section-heading { margin-bottom: 18px; }

      form { display: grid; gap: 16px; }

      label { display: grid; gap: 8px; color: #344054; font-size: 14px; font-weight: 700; }

      input, select, button {
        width: 100%;
        min-height: 46px;
        border-radius: 6px;
        font: inherit;
      }

      input, select {
        border: 1px solid #cfd7e3;
        background: #ffffff;
        padding: 10px 12px;
        color: #202124;
      }

      input:focus, select:focus {
        border-color: #2b6cb0;
        outline: 3px solid rgb(43 108 176 / 18%);
      }

      button, .button {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        min-height: 46px;
        border: 0;
        border-radius: 6px;
        padding: 11px 14px;
        font: inherit;
        font-weight: 800;
        text-decoration: none;
        cursor: pointer;
      }

      button, .button-primary {
        color: #ffffff;
        background: #1f2937;
      }

      .button-secondary {
        border: 1px solid #cfd7e3;
        color: #1f2937;
        background: #ffffff;
      }

      .actions { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 18px; }

      .details { display: grid; gap: 14px; margin: 0; }

      .details div {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 16px;
        border-bottom: 1px solid #edf0f5;
        padding-bottom: 12px;
      }

      dt { color: #637083; font-size: 14px; }

      dd { margin: 0; overflow-wrap: anywhere; text-align: right; font-weight: 700; }

      code {
        border-radius: 6px;
        background: #f3f6fa;
        padding: 2px 6px;
        font-size: 13px;
      }

      .status-badge {
        display: inline-flex;
        align-items: center;
        min-height: 28px;
        border-radius: 999px;
        padding: 4px 10px;
        font-size: 13px;
        font-weight: 800;
      }

      .status-created, .status-pending { background: #eef4ff; color: #2457a6; }
      .status-succeeded { background: #e8f7ef; color: #17633a; }
      .status-failed { background: #fff0f0; color: #9b2424; }

      .table-card { overflow: hidden; }

      .table-wrap { overflow-x: auto; }

      table { border-collapse: collapse; width: 100%; min-width: 760px; }

      th, td {
        border-bottom: 1px solid #edf0f5;
        padding: 12px 10px;
        text-align: left;
        vertical-align: middle;
      }

      th { color: #637083; font-size: 13px; font-weight: 800; }

      .action-link { color: #2457a6; font-weight: 800; text-decoration: none; }

      .empty-state, .empty-table { color: #5f6b7a; }

      .empty-table {
        border: 1px dashed #cfd7e3;
        border-radius: 8px;
        padding: 24px;
        text-align: center;
      }

      @media (max-width: 860px) {
        .hero { align-items: start; flex-direction: column; }
        .flow-steps { grid-auto-flow: row; }
        .grid-layout { grid-template-columns: 1fr; }
      }

      @media (max-width: 560px) {
        .page-shell { padding: 24px 14px; }
        .card { padding: 20px; }
        h1 { font-size: 28px; }
        .details div { align-items: start; flex-direction: column; gap: 6px; }
        dd { text-align: left; }
        .button, button { width: 100%; }
      }
    </style>
  </head>
  <body>
    <main class="page-shell">
      ${content}
    </main>
  </body>
</html>`;
}

function statusBadge(status) {
  return `<span class="status-badge status-${escapeHtml(status)}">${escapeHtml(formatStatus(status))}</span>`;
}

function formatStatus(status) {
  return `${status.charAt(0).toUpperCase()}${status.slice(1)}`;
}

function formatScenario(scenario) {
  if (scenario === 'card_success') {
    return 'Successful payment';
  }

  if (scenario === 'card_declined') {
    return 'Failed payment';
  }

  return scenario;
}

function formatAmount(payment) {
  return `${payment.amount} ${payment.currency}`;
}

function formatDateTime(value) {
  if (!value) {
    return 'Unknown';
  }

  return new Intl.DateTimeFormat('en', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value));
}

function readForm(request) {
  return readText(request).then((body) => new URLSearchParams(body));
}

function readJson(request) {
  return readText(request).then((body) => JSON.parse(body));
}

function readText(request) {
  return new Promise((resolve, reject) => {
    const chunks = [];

    request.on('data', (chunk) => chunks.push(chunk));
    request.on('end', () => resolve(Buffer.concat(chunks).toString('utf8')));
    request.on('error', reject);
  });
}

function redirect(response, location) {
  response.writeHead(303, { location });
  response.end();
}

function sendHtml(response, statusCode, body) {
  response.writeHead(statusCode, { 'content-type': 'text/html; charset=utf-8' });
  response.end(body);
}

function sendJson(response, statusCode, body) {
  response.writeHead(statusCode, { 'content-type': 'application/json; charset=utf-8' });
  response.end(JSON.stringify(body));
}

function escapeHtml(value) {
  return String(value)
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#39;');
}

if (import.meta.url === `file://${process.argv[1]}`) {
  const app = createMerchantApp();

  app.listen().then(() => {
    console.log(`Example merchant listening on http://${defaultConfig.host}:${defaultConfig.port}`);
  });

  const close = () => {
    app.close().then(() => process.exit(0));
  };

  process.on('SIGINT', close);
  process.on('SIGTERM', close);
}
