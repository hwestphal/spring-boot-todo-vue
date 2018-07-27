import { TodoListApi } from "client";
import Vue from "vue";
import main from "./main";

TodoListApi.prototype.todos = jest.fn().mockResolvedValue([]);

test("main function runs without warnings or errors", () => {
    const warn = jest.spyOn(global.console, "warn");
    const error = jest.spyOn(global.console, "error");
    document.body.innerHTML = "<div id=\"el\"></div>";
    main([], "/", "#el", "en", new Proxy({}, {
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
});
