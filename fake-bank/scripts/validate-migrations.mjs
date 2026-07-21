import { readFileSync, readdirSync } from 'node:fs';
import { join } from 'node:path';

const migrationsDir = new URL('../db/migrations/', import.meta.url);

const requiredObjects = [
  'CREATE TABLE payments',
  'CREATE TABLE payment_lifecycle_events',
  'CREATE TABLE scenario_execution_state',
  'CREATE TABLE webhook_delivery_attempts',
  'CREATE TYPE webhook_delivery_status AS ENUM',
  'CREATE INDEX payments_status_idx',
  'CREATE INDEX payment_lifecycle_events_payment_id_created_at_idx',
  'CREATE INDEX scenario_execution_state_scheduled_at_idx',
  'CREATE INDEX webhook_delivery_attempts_payment_id_created_at_idx',
];

const forbiddenColumnNames = [
  ['card', 'number'].join('_'),
  ['c', 'vv'].join(''),
  ['ex', 'piry'].join(''),
  ['p', 'an'].join(''),
];

const forbiddenPatterns = forbiddenColumnNames.map(
  (columnName) => new RegExp(`\\b${columnName}\\b`, 'i'),
);

const files = readdirSync(migrationsDir)
  .filter((file) => file.endsWith('.sql'))
  .sort();

if (files.length === 0) {
  throw new Error('No migration files found.');
}

let expectedNumber = 1;

for (const file of files) {
  const match = /^(\d{4})_[a-z0-9_]+\.sql$/.exec(file);

  if (!match) {
    throw new Error(`Invalid migration filename: ${file}`);
  }

  const actualNumber = Number(match[1]);

  if (actualNumber !== expectedNumber) {
    throw new Error(
      `Invalid migration order: expected ${String(expectedNumber).padStart(4, '0')}, got ${match[1]}`,
    );
  }

  expectedNumber += 1;

  const sql = readFileSync(join(migrationsDir.pathname, file), 'utf8');
  const trimmedSql = sql.trim();

  if (!trimmedSql.startsWith('BEGIN;')) {
    throw new Error(`Migration must start with BEGIN: ${file}`);
  }

  if (!trimmedSql.endsWith('COMMIT;')) {
    throw new Error(`Migration must end with COMMIT: ${file}`);
  }

  for (const pattern of forbiddenPatterns) {
    if (pattern.test(sql)) {
      throw new Error(`Migration stores forbidden card data field: ${file}`);
    }
  }
}

const initialMigration = readFileSync(join(migrationsDir.pathname, files[0]), 'utf8');

for (const requiredObject of requiredObjects) {
  if (!initialMigration.includes(requiredObject)) {
    throw new Error(`Initial migration is missing required object: ${requiredObject}`);
  }
}

const allMigrations = files
  .map((file) => readFileSync(join(migrationsDir.pathname, file), 'utf8'))
  .join('\n');

if (!allMigrations.includes("RENAME VALUE 'sent' TO 'delivered'")) {
  throw new Error('Migrations must normalize webhook delivery status to delivered.');
}

console.log(`Validated ${files.length} migration file(s).`);
