import pg from 'pg';

export type DbPool = Pick<pg.Pool, 'end' | 'query'>;

export function createPgPool(connectionString: string): pg.Pool {
  return new pg.Pool({ connectionString });
}
