import { TodoListApi } from "@Generated/openapi";
import main from "./main";

TodoListApi.prototype.todos = jest.fn().mockResolvedValue([]);

const warn = jest.spyOn(global.console, "warn");
const error = jest.spyOn(global.console, "error");

afterEach(() => {
    warn.mockReset();
    error.mockReset();
});

test.each(["en", "de"])(`main function runs without warnings or errors for locale "%s"`, (locale) => {
    document.body.innerHTML = "<div id=\"el\"></div>";
    main([], "/", "#el", locale, new Proxy({}, {
        get(target, key): string | undefined {
            if (typeof key === "string") {
                if (!key.startsWith("_")) {
                    return key;
                }
            }
        },
    }));
    expect(warn).not.toHaveBeenCalled();
    expect(error).not.toHaveBeenCalled();
});
