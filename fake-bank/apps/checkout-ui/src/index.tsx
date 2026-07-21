import React, { useEffect, useMemo, useState } from 'react';
import type { FormEvent } from 'react';
import { createRoot } from 'react-dom/client';
import './styles.css';

type PaymentStatus = 'created' | 'pending' | 'succeeded' | 'failed';

type Payment = {
  payment_id: string;
  merchant_id: string;
  merchant_reference?: string;
  amount: number;
  currency: string;
  payment_method: 'card';
  status: PaymentStatus;
  checkout_url: string;
  return_url: string;
  webhook_url: string;
  scenario?: string;
  metadata?: Record<string, string>;
  created_at: string;
  updated_at: string;
};

type CardForm = {
  number: string;
  expiry: string;
  cvv: string;
};

type ApiError = {
  error_code: string;
  message: string;
  request_id: string;
  details?: Record<string, unknown>;
};

type LoadState =
  | { kind: 'loading' }
  | { kind: 'ready'; payment: Payment }
  | { kind: 'error'; message: string };

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? '';

function App() {
  const paymentId = getPaymentIdFromPath(window.location.pathname);
  const [loadState, setLoadState] = useState<LoadState>({ kind: 'loading' });
  const [card, setCard] = useState<CardForm>({
    number: '',
    expiry: '',
    cvv: '',
  });
  const [formError, setFormError] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (!paymentId) {
      setLoadState({ kind: 'error', message: 'Payment identifier is missing.' });
      return;
    }

    loadPayment(paymentId)
      .then((payment) => {
        setLoadState({ kind: 'ready', payment });
      })
      .catch((error: unknown) => {
        setLoadState({ kind: 'error', message: getErrorMessage(error) });
      });
  }, [paymentId]);

  const payment = loadState.kind === 'ready' ? loadState.payment : null;
  const amountText = useMemo(() => {
    if (!payment) {
      return '';
    }

    return `${payment.amount.toFixed(2)} ${payment.currency}`;
  }, [payment]);

  function submitPayment(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    if (!payment) {
      return;
    }

    const validationError = validateCard(card);

    if (validationError) {
      setFormError(validationError);
      return;
    }

    setFormError(null);
    setIsSubmitting(true);

    confirmPayment(payment.payment_id, card)
      .then((updatedPayment) => {
        setLoadState({ kind: 'ready', payment: updatedPayment });
        setCard({ number: '', expiry: '', cvv: '' });
      })
      .catch((error: unknown) => {
        setFormError(getErrorMessage(error));
      })
      .finally(() => {
        setIsSubmitting(false);
      });
  }

  return (
    <main className="page-shell">
      <section className="checkout-panel" aria-labelledby="checkout-title">
        <div className="brand">Fake-bank</div>
        <h1 id="checkout-title">Payment</h1>

        {loadState.kind === 'loading' ? (
          <StatusBlock tone="neutral" title="Loading payment" />
        ) : null}

        {loadState.kind === 'error' ? (
          <StatusBlock tone="failed" title="Payment unavailable" detail={loadState.message} />
        ) : null}

        {payment ? (
          <>
            <dl className="payment-summary">
              <div>
                <dt>Merchant</dt>
                <dd>{payment.merchant_reference ?? payment.merchant_id}</dd>
              </div>
              <div>
                <dt>Amount</dt>
                <dd>{amountText}</dd>
              </div>
              <div>
                <dt>Status</dt>
                <dd>
                  <span className={`status-pill status-${payment.status}`}>
                    {formatStatus(payment.status)}
                  </span>
                </dd>
              </div>
            </dl>

            <PaymentState payment={payment} amountText={amountText} isSubmitting={isSubmitting} />

            {payment.status === 'created' || payment.status === 'pending' ? (
              <form className="card-form" onSubmit={submitPayment}>
                <label>
                  <span>Card number</span>
                  <input
                    inputMode="numeric"
                    autoComplete="off"
                    value={card.number}
                    onChange={(event) => setCard({ ...card, number: normalizeDigits(event.target.value) })}
                    placeholder="Card number"
                    maxLength={19}
                  />
                </label>

                <div className="form-row">
                  <label>
                    <span>Expiry</span>
                    <input
                      inputMode="numeric"
                      autoComplete="off"
                      value={card.expiry}
                      onChange={(event) => setCard({ ...card, expiry: event.target.value })}
                      placeholder="MM / YY"
                      maxLength={5}
                    />
                  </label>

                  <label>
                    <span>CVV</span>
                    <input
                      inputMode="numeric"
                      autoComplete="off"
                      value={card.cvv}
                      onChange={(event) => setCard({ ...card, cvv: normalizeDigits(event.target.value) })}
                      placeholder="CVC"
                      maxLength={4}
                    />
                  </label>
                </div>

                {formError ? <p className="form-error">{formError}</p> : null}

                <button type="submit" disabled={isSubmitting}>
                  {isSubmitting ? 'Processing...' : 'Pay'}
                </button>
              </form>
            ) : null}
          </>
        ) : null}
      </section>
    </main>
  );
}

function PaymentState(props: { payment: Payment; amountText: string; isSubmitting: boolean }) {
  if (props.isSubmitting || props.payment.status === 'pending') {
    return <StatusBlock tone="neutral" title="Payment is processing" />;
  }

  if (props.payment.status === 'succeeded') {
    return <SuccessResult payment={props.payment} amountText={props.amountText} />;
  }

  if (props.payment.status === 'failed') {
    return <StatusBlock tone="failed" title="Payment failed" />;
  }

  return null;
}

function SuccessResult(props: { payment: Payment; amountText: string }) {
  return (
    <div className="success-result">
      <StatusBlock
        tone="success"
        title="Payment completed"
        detail="Your payment was confirmed successfully."
      />
      <dl className="result-summary" aria-label="Payment confirmation summary">
        <div>
          <dt>Payment ID</dt>
          <dd>{props.payment.payment_id}</dd>
        </div>
        <div>
          <dt>Merchant</dt>
          <dd>{props.payment.merchant_reference ?? props.payment.merchant_id}</dd>
        </div>
        <div>
          <dt>Amount</dt>
          <dd>{props.amountText}</dd>
        </div>
      </dl>
      <a className="return-button" href={props.payment.return_url}>
        Return to merchant
      </a>
    </div>
  );
}

function StatusBlock(props: { tone: 'neutral' | 'success' | 'failed'; title: string; detail?: string }) {
  return (
    <div className={`status-block status-block-${props.tone}`}>
      <strong>{props.title}</strong>
      {props.detail ? <span>{props.detail}</span> : null}
    </div>
  );
}

function getPaymentIdFromPath(pathname: string): string | null {
  const match = /^\/checkout\/(pay_[A-Za-z0-9]+)$/.exec(pathname);
  return match?.[1] ?? null;
}

function normalizeDigits(value: string): string {
  return value.replace(/\D/g, '');
}

function validateCard(card: CardForm): string | null {
  if (!/^[0-9]{12,19}$/.test(card.number)) {
    return 'Enter a supported test card number.';
  }

  if (!/^(0[1-9]|1[0-2])\/[0-9]{2}$/.test(card.expiry)) {
    return 'Enter expiry in MM/YY format.';
  }

  if (!/^[0-9]{3,4}$/.test(card.cvv)) {
    return 'Enter a valid CVV.';
  }

  return null;
}

function formatStatus(status: PaymentStatus): string {
  return status.charAt(0).toUpperCase() + status.slice(1);
}

function loadPayment(paymentId: string): Promise<Payment> {
  return fetch(`${apiBaseUrl}/v1/payments/${paymentId}`)
    .then(parseJsonResponse<Payment>);
}

function confirmPayment(paymentId: string, card: CardForm): Promise<Payment> {
  return fetch(`${apiBaseUrl}/v1/payments/${paymentId}/confirm`, {
    method: 'POST',
    headers: {
      'content-type': 'application/json',
    },
    body: JSON.stringify({
      payment_method: 'card',
      card,
    }),
  }).then(parseJsonResponse<Payment>);
}

function parseJsonResponse<T>(response: Response): Promise<T> {
  return response.json().then((body: T | ApiError) => {
    if (!response.ok) {
      const apiError = body as ApiError;
      throw new Error(apiError.message);
    }

    return body as T;
  });
}

function getErrorMessage(error: unknown): string {
  return error instanceof Error ? error.message : 'Unexpected checkout error.';
}

createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
);
