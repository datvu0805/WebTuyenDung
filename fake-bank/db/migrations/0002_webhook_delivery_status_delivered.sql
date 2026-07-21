BEGIN;

ALTER TYPE webhook_delivery_status RENAME VALUE 'sent' TO 'delivered';

COMMIT;
