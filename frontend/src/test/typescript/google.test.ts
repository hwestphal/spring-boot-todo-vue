import puppeteer, { Browser, Page } from "puppeteer";

let browser: Browser;
let page: Page;

beforeAll(async () => {
    browser = await puppeteer.launch();
});

beforeEach(async () => {
    page = await browser.newPage();
});

test("load google.com", async () => {
    await page.goto("https://www.google.com");
    const text = await page.evaluate(() => document.body.textContent);
    expect(text).toContain("google");
});

afterAll(async () => {
    await browser.close();
});
