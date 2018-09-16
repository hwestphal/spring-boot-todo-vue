import { Todo } from "@Generated/openapi";
import { config, mount } from "@vue/test-utils";
import { createRenderer } from "vue-server-renderer";
import App from "./App.vue";
import { MessageBox } from "./elements";
import { reactive } from "./reactive";
import { Suggestions } from "./state/suggestions";
import { ConflictError, TodoList } from "./state/todoList";

config.logModifiedComponents = false;

const renderer = createRenderer();

const mockConfirm = MessageBox.confirm = jest.fn();
const mockAdd = jest.fn();
const mockRemove = jest.fn();
const mockRefresh = jest.fn();
const mockReset = jest.fn();
const mockSave = jest.fn();

@reactive
class TodoListFake {
    constructor(public todos: Todo[] = [], public changed = false) { }
    add(...args: any[]) {
        mockAdd(...args);
    }
    remove(...args: any[]) {
        mockRemove(...args);
    }
    async refresh() {
        await mockRefresh();
    }
    reset() {
        mockReset();
    }
    async save() {
        await mockSave();
    }
}

const todoListFake = new TodoListFake();

afterEach(() => {
    mockConfirm.mockReset();
    mockAdd.mockReset();
    mockRemove.mockReset();
    mockRefresh.mockReset();
    mockReset.mockReset();
    mockSave.mockReset();
    todoListFake.todos = [];
    todoListFake.changed = false;
});

function app() {
    return mount(App, {
        mocks: {
            $t: (key: string) => key,
            $tc: (key: string) => key,
        },
        provide: {
            [Suggestions.name]: {
                openSuggestions: [],
            },
            [TodoList.name]: todoListFake,
        },
    }).vm;
}

describe("todolist app", () => {
    it("is initially invalid", () => {
        expect(app().valid).toBe(false);
    });

    it("becomes valid for sufficient long todo name", () => {
        const vm = app();
        vm.newTodo = "1234";
        expect(vm.valid).toBe(true);
    });

    it("adds valid new todo", () => {
        const vm = app();
        vm.newTodo = "1234";
        vm.addNewTodo();
        expect(mockAdd).toBeCalledWith("1234");
        expect(vm.newTodo).toBe("");
    });

    it("ignores invalid new todo", () => {
        const vm = app();
        vm.newTodo = "123";
        vm.addNewTodo();
        expect(mockAdd).not.toBeCalled();
        expect(vm.newTodo).toBe("123");
    });

    it("closes todo", async () => {
        const vm = app();
        todoListFake.todos.push({ title: "XXX", completed: false });
        vm.close(todoListFake.todos[0]);
        expect(await renderer.renderToString(vm)).toMatchSnapshot();
    });

    it("opens todo", async () => {
        const vm = app();
        todoListFake.todos.push({ title: "XXX", completed: true });
        vm.open(todoListFake.todos[0]);
        expect(await renderer.renderToString(vm)).toMatchSnapshot();
    });

    it("closes all todos", async () => {
        const vm = app();
        todoListFake.todos.push({ title: "XXX", completed: false });
        vm.closeAll();
        expect(await renderer.renderToString(vm)).toMatchSnapshot();
    });

    it("resets todo list", () => {
        app().reset();
        expect(mockReset).toBeCalled();
    });

    it("removes todo", () => {
        const todo = { title: "todo", completed: true };
        app().remove(todo);
        expect(mockRemove).toBeCalledWith(todo);
    });

    it("removes error class on valid input", async () => {
        const vm = app();
        expect(await renderer.renderToString(vm)).toMatchSnapshot();
        vm.newTodo = "1234";
        expect(await renderer.renderToString(vm)).toMatchSnapshot();
    });

    it("saves todo list", async () => {
        await app().save();
        expect(mockSave).toBeCalled();
    });

    it("handles unknow error on save", async () => {
        const error = {};
        mockSave.mockRejectedValue(error);
        await expect(app().save()).rejects.toBe(error);
    });

    it("refreshes on conflict error", async () => {
        mockSave.mockRejectedValue(new ConflictError());
        await app().save();
        expect(mockRefresh).toBeCalled();
    });

    it("does not refresh on conflict error", async () => {
        mockSave.mockRejectedValue(new ConflictError());
        mockConfirm.mockRejectedValue({});
        await app().save();
        expect(mockRefresh).not.toBeCalled();
    });
});
