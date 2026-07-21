-- Local Fake-bank payment fields for the external application database.
-- Apply this migration once, before deploying the Fake-bank integration. It only
-- adds nullable columns and indexes and does not alter or delete existing data.
-- The application reports a clear payment-creation failure until this is applied.

ALTER TABLE transactions
    ADD COLUMN IF NOT EXISTS package_id BIGINT,
    ADD COLUMN IF NOT EXISTS txn_ref VARCHAR(200),
    ADD COLUMN IF NOT EXISTS payment_status VARCHAR(16),
    ADD COLUMN IF NOT EXISTS payment_provider VARCHAR(32),
    ADD COLUMN IF NOT EXISTS provider_transaction_id VARCHAR(128);

UPDATE transactions
SET payment_status = CASE status
    WHEN 1 THEN 'SUCCESS'
    WHEN 2 THEN 'FAILED'
    ELSE 'PENDING'
END
WHERE payment_status IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_transactions_txn_ref
    ON transactions (txn_ref)
    WHERE txn_ref IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_transactions_provider_transaction_id
    ON transactions (payment_provider, provider_transaction_id)
    WHERE provider_transaction_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS ix_transactions_txn_ref_user
    ON transactions (txn_ref, user_id);
