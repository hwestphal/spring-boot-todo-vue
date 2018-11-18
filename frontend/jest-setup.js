if (!global.fetch) {
    const fetch = require("node-fetch");
    global.fetch = fetch;
    global.Request = fetch.Request;
    global.Response = fetch.Response;
    global.Headers = fetch.Headers;
}
