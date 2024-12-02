import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  build: {
    chunkSizeWarningLimit: 2500,
  },
  // server: {
  //   proxy: {
  //     '/api': {
  //       // 后台地址
  //       target: 'http://120.46.95.186:8888/',
  //       changeOrigin: true,
  //       rewrite: path => path.replace(/^\/api/, '')
  //     },
  //   }
  // }
})
