import { reactive } from "../reactive";
import { Suggestions } from "./suggestions";

@reactive
class TodoListFake {
    constructor(public todos: any[] = []) {
    }
}

describe("suggestions", () => {
    it("are filtered by todos", () => {
        const todoList = new TodoListFake();
        const suggestions = new Suggestions(["some text", "more text"], todoList as any);
        expect(suggestions.openSuggestions).toHaveLength(2);
        expect(suggestions.openSuggestions).toContain("some text");
        expect(suggestions.openSuggestions).toContain("more text");
        todoList.todos.push({ title: " More  Text" });
        expect(suggestions.openSuggestions).toHaveLength(1);
        expect(suggestions.openSuggestions).toContain("some text");
    });
});
