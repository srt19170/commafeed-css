import { lingui } from "@lingui/vite-plugin"
import react from "@vitejs/plugin-react"
import { visualizer } from "rollup-plugin-visualizer"
import { type PluginOption, defineConfig } from "vite"
import checker from "vite-plugin-checker"
import tsconfigPaths from "vite-tsconfig-paths"

// https://vitejs.dev/config/
export default defineConfig(() => ({
    plugins: [
        customCodeInjector,
        react({
            babel: {
                plugins: ["@lingui/babel-plugin-lingui-macro"],
            },
        }),
        lingui(),
        tsconfigPaths(),
        visualizer(),
        checker({
            typescript: true,
            biome: {
                command: "check",
            },
        }),
    ],
    base: "./",
    server: {
        port: 8082,
        proxy: {
            "/rest": "http://localhost:8083",
            "/next": "http://localhost:8083",
            "/ws": "ws://localhost:8083",
            "/openapi.json": "http://localhost:8083",
            "/custom_css.css": "http://localhost:8083",
            "/custom_js.js": "http://localhost:8083",
            "/j_security_check": "http://localhost:8083",
            "/logout": "http://localhost:8083",
        },
    },
    build: {
        chunkSizeWarningLimit: 3500,
        rollupOptions: {
            output: {
                manualChunks: id => {
                    // output mantine as its own chunk because it is quite large
                    if (id.includes("@mantine")) {
                        return "mantine"
                    }
                },
            },
        },
    },
    test: {
        environment: "jsdom",
        globals: true,
        setupFiles: "./src/setupTests.ts",
    },
}))

// inject custom js and css links in html
const customCodeInjector: PluginOption = {
    name: "customCodeInjector",
    transformIndexHtml: html => {
        return {
            html,
            tags: [
                {
                    tag: "script",
                    attrs: {
                        src: "custom_js.js",
                    },
                    injectTo: "body",
                },
                {
                    tag: "link",
                    attrs: {
                        rel: "stylesheet",
                        href: "custom_css.css",
                    },
                    injectTo: "head",
                },
            ],
        }
    },
}
