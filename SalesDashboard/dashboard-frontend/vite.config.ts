import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
      outDir: '../src/main/resources/public',
      emptyOutDir: true,
  },
    server: {
      proxy: {
          "/dashboard": "http://localhost:9024"
      }
    }
})
