import { mount, VueClass } from "@vue/test-utils";
import { Todo, TodoListApi } from "client";
import AutoComplete from "./autocomplete.vue";
import { MessageBox } from "./elements";
import TodolistClass from "./todolist";
import Todolist from "./todolist.vue";

const mockGetTodos = jest.fn();
const mockOverwriteTodos = jest.fn();
const mockConfirm = MessageBox.confirm = jest.fn();

afterEach(() => {
    mockGetTodos.mockReset();
    mockOverwriteTodos.mockReset();
    mockConfirm.mockReset();
});

async function todolist(todoList: Todo[] = [], suggestions: string[] = []) {
    mockGetTodos.mockResolvedValue(todoList);
    const c = mount(Todolist as VueClass<TodolistClass>, {
        mocks: {
            $t: (key: string) => key,
            $tc: (key: string) => key,
        },
        propsData: {
            suggestions,
        },
        provide: {
            [TodoListApi.name]: {
                overwriteTodos: mockOverwriteTodos,
                todos: mockGetTodos,
            },
        },
    });
    // flush pending promises
    await new Promise((resolve) => setTimeout(resolve));
    return c;
}

describe("todolist", () => {
    it("is initially invalid", async () => {
        expect((await todolist()).vm.valid).toBe(false);
    });

    it("becomes valid for sufficient long todo name", async () => {
        const vm = (await todolist()).vm;
        vm.newTodo = "1234";
        expect(vm.valid).toBe(true);
    });

    it("adds valid new todo", async () => {
        const vm = (await todolist()).vm;
        vm.newTodo = "1234";
        vm.addNewTodo();
        expect(vm.todos).toHaveLength(1);
        const todo = vm.todos[0];
        expect(todo.title).toBe("1234");
        expect(todo.completed).toBe(false);
    });

    it("ignores invalid new todo", async () => {
        const vm = (await todolist()).vm;
        vm.newTodo = "123";
        vm.addNewTodo();
        expect(vm.todos).toHaveLength(0);
    });

    it("filters out a suggestion", async () => {
        const vm = (await todolist([{
            completed: false,
            title: "  wash  the car ",
        }], ["Feed the dog", " Wash the  car"])).vm;
        expect(vm.openSuggestions).toHaveLength(1);
        expect(vm.openSuggestions[0]).toBe("Feed the dog");
    });

    it("closes an open todo", async () => {
        const vm = (await todolist([{
            completed: false,
            title: "a todo",
        }])).vm;
        expect(vm.openTodos).toHaveLength(1);
        expect(vm.doneTodos).toHaveLength(0);
        vm.close(vm.openTodos[0]);
        expect(vm.openTodos).toHaveLength(0);
        expect(vm.doneTodos).toHaveLength(1);
    });

    it("opens a closed todo", async () => {
        const vm = (await todolist([{
            completed: true,
            title: "a todo",
        }])).vm;
        expect(vm.openTodos).toHaveLength(0);
        expect(vm.doneTodos).toHaveLength(1);
        vm.open(vm.doneTodos[0]);
        expect(vm.openTodos).toHaveLength(1);
        expect(vm.doneTodos).toHaveLength(0);
    });

    it("closes all open todos", async () => {
        const vm = (await todolist([{
            completed: false,
            title: "a todo",
        }, {
            completed: false,
            title: "another todo",
        }])).vm;
        vm.closeAll();
        expect(vm.openTodos).toHaveLength(0);
        expect(vm.doneTodos).toHaveLength(2);
    });

    it("removes a todo", async () => {
        const vm = (await todolist([{
            completed: false,
            title: "a todo",
        }, {
            completed: false,
            title: "to be removed",
        }, {
            completed: false,
            title: "another todo",
        }])).vm;
        expect(vm.todos).toHaveLength(3);
        vm.remove(vm.todos[1]);
        expect(vm.todos).toHaveLength(2);
        expect(vm.todos[0].title).toBe("a todo");
        expect(vm.todos[1].title).toBe("another todo");
    });

    it("resets to initial state", async () => {
        const vm = (await todolist()).vm;
        expect(vm.changed).toBe(false);
        vm.newTodo = "1234";
        vm.addNewTodo();
        expect(vm.todos).toHaveLength(1);
        expect(vm.changed).toBe(true);
        vm.reset();
        expect(vm.todos).toHaveLength(0);
        expect(vm.changed).toBe(false);
    });

    it("submits todos on save", async () => {
        const vm = (await todolist()).vm;
        vm.newTodo = "1234";
        vm.addNewTodo();
        await vm.save();
        expect(mockOverwriteTodos).toBeCalledWith([{ title: "1234", completed: false }]);
        expect(vm.todos).toHaveLength(0);
        expect(vm.changed).toBe(false);
    });

    it("removes error class on valid input", async () => {
        const wrapper = await todolist();
        const vm = wrapper.vm;
        const errorClass = (vm as any).$style.error;
        const autocomplete = wrapper.find(AutoComplete);
        expect(autocomplete.classes()).toContain(errorClass);
        vm.newTodo = "1234";
        expect(autocomplete.classes()).not.toContain(errorClass);
    });

    it("handles error on save", async () => {
        const vm = (await todolist()).vm;
        vm.newTodo = "1234";
        vm.addNewTodo();
        const error = {};
        mockOverwriteTodos.mockRejectedValueOnce(error);
        await expect(vm.save()).rejects.toBe(error);
        expect(vm.changed).toBe(true);
    });

    it("handles invalid response on save", async () => {
        const vm = (await todolist()).vm;
        vm.newTodo = "1234";
        vm.addNewTodo();
        const response = new Response();
        mockOverwriteTodos.mockRejectedValueOnce(response);
        await expect(vm.save()).rejects.toBe(response);
        expect(vm.changed).toBe(true);
    });

    it("handles 409 response on save", async () => {
        const vm = (await todolist()).vm;
        vm.newTodo = "1234";
        vm.addNewTodo();
        mockOverwriteTodos.mockRejectedValueOnce(new Response("error", { status: 409 }));
        mockConfirm.mockRejectedValueOnce({});
        await vm.save();
        expect(vm.changed).toBe(true);
        mockOverwriteTodos.mockRejectedValueOnce(new Response("error", { status: 409 }));
        mockConfirm.mockResolvedValueOnce({});
        await vm.save();
        expect(vm.changed).toBe(false);
    });
});
