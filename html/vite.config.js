import {fileURLToPath, URL} from 'node:url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue()],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url))
        }
    },
    css: {
        preprocessorOptions: {
            less: {
                javascriptEnabled: true,
            }
        },
    },
    build: {
        outDir: '../src/main/resources/public',
        // outDir: '../target/classes/public',
        emptyOutDir: true
    },
    server: {
        proxy: {
            '/api': 'http://localhost:8071'
        },
    },
})
