import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://backend-load-471836027.us-east-1.elb.amazonaws.com',
        changeOrigin: true,
      },
    },
  },
});
