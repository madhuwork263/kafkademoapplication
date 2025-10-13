FROM mcr.microsoft.com/playwright:v1.56.0-jammy

WORKDIR /tests

# Copy package.json and tests
COPY package*.json ./
COPY playwright.config.js ./
COPY tests ./tests

RUN npm install

# Run Playwright tests
CMD ["npx", "playwright", "test"]
