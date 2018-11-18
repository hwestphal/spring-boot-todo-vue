import { Todo } from "@Generated/openapi";
import { ConflictError, TodoList } from "./todoList";

const mockGetTodos = jest.fn();
const mockOverwriteTodos = jest.fn();

afterEach(() => {
    mockGetTodos.mockReset();
    mockOverwriteTodos.mockReset();
});

async function createTodoList(todos: Todo[]) {
    mockGetTodos.mockResolvedValue(todos);
    const todoList = new TodoList({
        overwriteTodos: mockOverwriteTodos,
        todos: mockGetTodos,
    } as any);
    // flush pending promises
    await new Promise((resolve) => setTimeout(resolve));
    return todoList;
}

describe("todolist", () => {
    it("is correctly initialized", async () => {
        const todos = [{ title: "todo", completed: false }];
        const todoList = await createTodoList(todos);
        expect(todoList.todos).toEqual(todos);
        expect(todoList.changed).toBe(false);
    });

    it("adds a todo", async () => {
        const todoList = await createTodoList([]);
        expect(todoList.changed).toBe(false);
        todoList.add("todo");
        expect(todoList.todos).toContainEqual({ title: "todo", completed: false });
        expect(todoList.changed).toBe(true);
    });

    it("removes a todo", async () => {
        const todo: Todo = { title: "todo", completed: false };
        const todoList = await createTodoList([todo]);
        todoList.remove(todo);
        expect(todoList.changed).toBe(false);
        todoList.remove(todoList.todos[0]);
        expect(todoList.todos).toHaveLength(0);
        expect(todoList.changed).toBe(true);
    });

    it("detects a changed todo", async () => {
        const todoList = await createTodoList([{ title: "todo", completed: false }]);
        expect(todoList.changed).toBe(false);
        const todo = todoList.todos[0];
        todo.completed = true;
        expect(todoList.changed).toBe(true);
        todo.completed = false;
        expect(todoList.changed).toBe(false);
    });

    it("saves todos", async () => {
        const todoList = await createTodoList([]);
        todoList.add("todo");
        expect(todoList.changed).toBe(true);
        await todoList.save();
        expect(mockOverwriteTodos).toBeCalledWith([{ title: "todo", completed: false }]);
        expect(todoList.todos).toHaveLength(0);
        expect(todoList.changed).toBe(false);
    });

    it("handles unknown error during save", async () => {
        const todoList = await createTodoList([]);
        todoList.add("todo");
        expect(todoList.changed).toBe(true);
        const error = {};
        mockOverwriteTodos.mockRejectedValueOnce(error);
        await expect(todoList.save()).rejects.toBe(error);
        expect(todoList.changed).toBe(true);
    });

    it("handles invalid response during save", async () => {
        const todoList = await createTodoList([]);
        todoList.add("todo");
        expect(todoList.changed).toBe(true);
        const response = new Response();
        mockOverwriteTodos.mockRejectedValueOnce(response);
        await expect(todoList.save()).rejects.toBe(response);
        expect(todoList.changed).toBe(true);
    });

    it("handles 409 response during save", async () => {
        const todoList = await createTodoList([]);
        todoList.add("todo");
        expect(todoList.changed).toBe(true);
        mockOverwriteTodos.mockRejectedValueOnce(new Response("error", { status: 409 }));
        await expect(todoList.save()).rejects.toBeInstanceOf(ConflictError);
        expect(todoList.changed).toBe(true);
    });
});
