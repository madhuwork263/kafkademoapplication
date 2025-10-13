// playwright.config.js
const { defineConfig } = require('@playwright/test');

module.exports = defineConfig({
    testDir: './tests', // 👈 folder where your .spec.js files are
    testMatch: ['**/*.spec.js'], // 👈 ensures Playwright picks up files ending in .spec.js
    reporter: [['html', { open: 'never' }]],
    use: {
        headless: false, // set to true if you don’t want the browser to open
    },
});
