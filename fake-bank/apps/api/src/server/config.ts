export type ApiConfig = {
  host: string;
  port: number;
  publicBaseUrl: string;
  checkoutBaseUrl: string;
  databaseUrl: string;
};

export function loadConfig(env: NodeJS.ProcessEnv = process.env): ApiConfig {
  const host = env.API_HOST ?? 'localhost';
  const port = parsePort(env.API_PORT ?? '8080');
  const publicBaseUrl = env.API_PUBLIC_BASE_URL ?? `http://${host}:${port}`;
  const checkoutBaseUrl =
    env.CHECKOUT_PUBLIC_BASE_URL
    ?? `http://${env.CHECKOUT_UI_HOST ?? 'localhost'}:${parsePort(env.CHECKOUT_UI_PORT ?? '5173')}`;
  const databaseUrl =
    env.DATABASE_URL ?? 'postgres://fake_bank:fake_bank@localhost:55432/fake_bank';

  return {
    host,
    port,
    publicBaseUrl,
    checkoutBaseUrl,
    databaseUrl,
  };
}

function parsePort(value: string): number {
  const port = Number(value);

  if (!Number.isInteger(port) || port <= 0 || port > 65535) {
    throw new Error(`Invalid API_PORT: ${value}`);
  }

  return port;
}
