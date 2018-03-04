import { shallow } from "@vue/test-utils";
import { default as Todolist, ITodo } from "./todolist.vue";

function todolist(todoList: ITodo[] = [], suggestions: string[] = []) {
  return shallow(Todolist, {
    mocks: {
      $t: jest.fn(),
      $tc: jest.fn(),
    },
    propsData: {
      suggestions,
      todoList,
    },
    stubs: ["auto-complete"],
  });
}

describe("todolist", () => {
  it("is initially invalid", () => {
    expect(todolist().vm.valid).toBe(false);
  });

  it("becomes valid for sufficient long todo name", () => {
    const vm = todolist().vm;
    vm.newTodo = "1234";
    expect(vm.valid).toBe(true);
  });

  it("adds valid new todo", () => {
    const vm = todolist().vm;
    vm.newTodo = "1234";
    vm.addNewTodo();
    expect(vm.todos).toHaveLength(1);
    const todo = vm.todos[0];
    expect(todo.title).toBe("1234");
    expect(todo.completed).toBe(false);
  });

  it("ignores invalid new todo", () => {
    const vm = todolist().vm;
    vm.newTodo = "123";
    vm.addNewTodo();
    expect(vm.todos).toHaveLength(0);
  });

  it("filters out a suggestion", () => {
    const vm = todolist([{
      completed: false,
      title: "  wash  the car ",
    }], ["Feed the dog", " Wash the  car"]).vm;
    expect(vm.openSuggestions).toHaveLength(1);
    expect(vm.openSuggestions[0]).toBe("Feed the dog");
  });

  it("closes an open todo", () => {
    const vm = todolist([{
      completed: false,
      title: "a todo",
    }]).vm;
    expect(vm.openTodos).toHaveLength(1);
    expect(vm.doneTodos).toHaveLength(0);
    vm.close(vm.openTodos[0]);
    expect(vm.openTodos).toHaveLength(0);
    expect(vm.doneTodos).toHaveLength(1);
  });

  it("opens a closed todo", () => {
    const vm = todolist([{
      completed: true,
      title: "a todo",
    }]).vm;
    expect(vm.openTodos).toHaveLength(0);
    expect(vm.doneTodos).toHaveLength(1);
    vm.open(vm.doneTodos[0]);
    expect(vm.openTodos).toHaveLength(1);
    expect(vm.doneTodos).toHaveLength(0);
  });

  it("closes all open todos", () => {
    const vm = todolist([{
      completed: false,
      title: "a todo",
    }, {
      completed: false,
      title: "another todo",
    }]).vm;
    vm.closeAll();
    expect(vm.openTodos).toHaveLength(0);
    expect(vm.doneTodos).toHaveLength(2);
  });

  it("removes a todo", () => {
    const vm = todolist([{
      completed: false,
      title: "a todo",
    }, {
      completed: false,
      title: "to be removed",
    }, {
      completed: false,
      title: "another todo",
    }]).vm;
    expect(vm.todos).toHaveLength(3);
    vm.remove(vm.todos[1]);
    expect(vm.todos).toHaveLength(2);
    expect(vm.todos[0].title).toBe("a todo");
    expect(vm.todos[1].title).toBe("another todo");
  });

  it("resets to initial state", () => {
    const vm = todolist().vm;
    expect(vm.changed).toBe(false);
    vm.newTodo = "1234";
    vm.addNewTodo();
    expect(vm.todos).toHaveLength(1);
    expect(vm.changed).toBe(true);
    vm.reset();
    expect(vm.todos).toHaveLength(0);
    expect(vm.changed).toBe(false);
  });

  it("submits a form on save", () => {
    const wrapper = todolist();
    const submit = (wrapper.find({ ref: "form" }).element as HTMLFormElement).submit = jest.fn();
    wrapper.vm.save();
    expect(submit).toHaveBeenCalled();
  });
});
