import { reactive } from "../reactive";
import { TodoList } from "./todoList";

function normalize(s: string) {
    return s.trim().toLowerCase().split(/\s+/).join(" ");
}

@reactive
export class Suggestions {
    constructor(private suggestions: string[], private todoList: TodoList) {
    }

    get openSuggestions() {
        return this.suggestions.filter((s) => !this.todoList.todos.some((t) => normalize(t.title) === normalize(s)));
    }
}
