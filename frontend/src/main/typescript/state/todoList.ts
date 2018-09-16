import { Todo, TodoListApi } from "@Generated/openapi";
import { reactive } from "../reactive";

export class ConflictError extends Error {
}

@reactive({ afterCreation: "refresh" })
export class TodoList {
    private localTodos: Todo[] = [];
    private savedTodos!: Todo[];

    constructor(private todoListApi: TodoListApi) {
    }

    get todos() {
        return this.localTodos;
    }

    get changed() {
        return JSON.stringify(this.localTodos) !== JSON.stringify(this.savedTodos);
    }

    remove(todo: Todo) {
        const i = this.localTodos.indexOf(todo);
        if (i >= 0) {
            this.localTodos.splice(i, 1);
        }
    }

    add(todo: string) {
        this.localTodos.push({
            completed: false,
            title: todo,
        });
    }

    reset() {
        this.localTodos = JSON.parse(JSON.stringify(this.savedTodos));
    }

    async save() {
        try {
            await this.todoListApi.overwriteTodos(this.localTodos);
        } catch (error) {
            if (error instanceof Response) {
                const { status } = error;
                if (status === 409) {
                    throw new ConflictError();
                }
            }
            throw error;
        }
        await this.refresh();
    }

    async refresh() {
        this.savedTodos = await this.todoListApi.todos();
        this.reset();
    }
}
