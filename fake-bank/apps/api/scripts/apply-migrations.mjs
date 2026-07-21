import { readFile } from 'node:fs/promises';
import { readdir } from 'node:fs/promises';
import { dirname, join, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';
import pg from 'pg';

const scriptDir = dirname(fileURLToPath(import.meta.url));
const repositoryRoot = resolve(scriptDir, '../../..');
const migrationsDir = join(repositoryRoot, 'db/migrations');
const databaseUrl =
  process.env.DATABASE_URL ?? 'postgres://fake_bank:fake_bank@localhost:55432/fake_bank';

const client = new pg.Client({ connectionString: databaseUrl });

readdir(migrationsDir)
  .then((entries) => entries.filter((entry) => entry.endsWith('.sql')).sort())
  .then((migrationFiles) => client.connect()
    .then(() => ensureMigrationTable(client))
    .then(() => migrationFiles.reduce((chain, migrationFile) => {
      return chain.then(() => applyMigration(client, migrationFile));
    }, Promise.resolve()))
    .then(() => {
      console.log(`Checked ${migrationFiles.length} migration file(s).`);
    }))
  .finally(() => client.end());

function ensureMigrationTable(client) {
  return client.query(`
    CREATE TABLE IF NOT EXISTS schema_migrations (
      migration_name text PRIMARY KEY,
      applied_at timestamptz NOT NULL DEFAULT now()
    )
  `);
}

function applyMigration(client, migrationFile) {
  const migrationPath = join(migrationsDir, migrationFile);

  return client
    .query('SELECT migration_name FROM schema_migrations WHERE migration_name = $1', [
      migrationFile,
    ])
    .then((result) => {
      if (result.rowCount > 0) {
        console.log(`Skipped migration: ${migrationFile}`);
        return null;
      }

      return isMigrationAlreadyApplied(client, migrationFile).then((alreadyApplied) => {
        if (alreadyApplied) {
          return recordMigration(client, migrationFile).then(() => {
            console.log(`Recorded existing migration: ${migrationFile}`);
          });
        }

        return readFile(migrationPath, 'utf8')
          .then((sql) => client.query(sql))
          .then(() => recordMigration(client, migrationFile))
          .then(() => {
            console.log(`Applied migration: ${migrationFile}`);
          });
      });
    });
}

function isMigrationAlreadyApplied(client, migrationFile) {
  if (migrationFile === '0001_payment_persistence.sql') {
    return client
      .query("SELECT to_regclass('public.payments') AS relation_name")
      .then((result) => result.rows[0]?.relation_name === 'payments');
  }

  if (migrationFile === '0002_webhook_delivery_status_delivered.sql') {
    return client
      .query(`
        SELECT 1
        FROM pg_enum
        JOIN pg_type ON pg_enum.enumtypid = pg_type.oid
        WHERE pg_type.typname = 'webhook_delivery_status'
          AND pg_enum.enumlabel = 'delivered'
      `)
      .then((result) => result.rowCount > 0);
  }

  return Promise.resolve(false);
}

function recordMigration(client, migrationFile) {
  return client.query(
    'INSERT INTO schema_migrations (migration_name) VALUES ($1) ON CONFLICT DO NOTHING',
    [migrationFile],
  );
}
