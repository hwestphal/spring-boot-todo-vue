import puppeteer, { Browser, ConsoleMessage, Page } from "puppeteer";
import path from "./path";
import serve, { IServer } from "./server";

let server: IServer;
let browser: Browser;
let page: Page;
let errors: ConsoleMessage[];
let warnings: ConsoleMessage[];

beforeAll(async () => {
    server = await serve(path("src/test/resources"), path("target/classes/static"));
    browser = await puppeteer.launch(puppeteerOptions());
});

beforeEach(async () => {
    page = await browser.newPage();
    errors = [];
    warnings = [];
    page.on("console", (msg) => {
        const type = msg.type();
        if (type === "error") {
            errors.push(msg);
        } else if (type === "warning") {
            warnings.push(msg);
        }
    });
    await page.goto(`http://localhost:${server.port}/`, { waitUntil: "networkidle0" });
});

test("take screenshot", async () => {
    await page.screenshot({ path: path("target", "puppeteer-screenshot.png") });
    expect(errors).toHaveLength(0);
    // expect only warnings for missing i18n keys
    warnings.forEach((m) => expect(m.text()).toMatch(/^\[vue-i18n\] /));
});

afterAll(async () => {
    await browser.close();
    await server.close();
});

function puppeteerOptions() {
    const slowMo = Number(process.env.PUPPETEER_SLOWMO);
    return {
        headless: isNaN(slowMo),
        slowMo,
    };
}

jest.setTimeout(60000);
