// playwright.config.js
const { defineConfig } = require('@playwright/test');

module.exports = defineConfig({
    testDir: './tests',              // ✅ folder with test files
    testMatch: ['**/*.spec.js'],     // ✅ run only .spec.js files
    reporter: [['html', { open: 'never' }]], // ✅ generate HTML report
    use: {
        headless: true,                // ✅ run in CI-safe headless mode
        viewport: { width: 1280, height: 720 },
        ignoreHTTPSErrors: true,
        video: 'on-first-retry',       // optional (good for debugging failed builds)
    },
});
