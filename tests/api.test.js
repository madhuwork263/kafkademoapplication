import { test, expect } from '@playwright/test';

test('Check Spring Boot health endpoint', async ({ request }) => {
    const response = await request.get('http://localhost:8080/actuator/health');
    expect(response.ok()).toBeTruthy();
    const json = await response.json();
    console.log('Health check response:', json);
    expect(json.status).toBe('UP');
});
