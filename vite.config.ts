import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

const additionalAllowedHosts = (
  process.env.__VITE_ADDITIONAL_SERVER_ALLOWED_HOSTS ?? ''
)
  .split(',')
  .map((host) => host.trim())
  .filter(Boolean);

const allowTryCloudflare =
  process.env.__VITE_ALLOW_TRYCLOUDFLARE === 'true';

const allowedHosts = allowTryCloudflare
  ? ['.trycloudflare.com', ...additionalAllowedHosts]
  : additionalAllowedHosts;

export default defineConfig({
  plugins: [react()],
  server: {
    host: '0.0.0.0',
    port: 5173,
    strictPort: true,
    allowedHosts,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  preview: {
    host: '0.0.0.0',
    port: 4173,
    strictPort: true,
    allowedHosts,
  },
});
