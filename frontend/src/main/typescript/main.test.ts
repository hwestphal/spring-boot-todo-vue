import { TodoListApi } from "@Generated/openapi";
import main from "./main";

TodoListApi.prototype.todos = jest.fn().mockResolvedValue([]);

const warn = jest.spyOn(global.console, "warn");
const error = jest.spyOn(global.console, "error");

afterEach(() => {
    warn.mockReset();
    error.mockReset();
});

["en", "de"].forEach((locale) => test(`main function runs without warnings or errors for locale \"${locale}\"`, () => {
    document.body.innerHTML = "<div id=\"el\"></div>";
    main([], "/", "#el", locale, new Proxy({}, {
        get(target, key): string | undefined {
            if (typeof key === "string") {
                const value = key as string;
                if (!value.startsWith("_")) {
                    return value;
                }
            }
        },
    }));
    expect(warn).not.toHaveBeenCalled();
    expect(error).not.toHaveBeenCalled();
}));
