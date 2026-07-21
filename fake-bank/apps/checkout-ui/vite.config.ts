import react from '@vitejs/plugin-react';
import { defineConfig, loadEnv } from 'vite';

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '');
  const apiBaseUrl = env.VITE_API_BASE_URL ?? env.API_PUBLIC_BASE_URL ?? 'http://localhost:8080';

  return {
    plugins: [react()],
    server: {
      host: env.CHECKOUT_UI_HOST ?? 'localhost',
      port: Number(env.CHECKOUT_UI_PORT ?? 5173),
      proxy: {
        '/v1': apiBaseUrl,
      },
    },
  };
});
