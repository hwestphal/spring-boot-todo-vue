import Vue from "vue";
import { default as Todolist, ITodo } from "./todolist";

function todolist(todoList: ITodo[], suggestions?: string[]) {
  return new Todolist({
    propsData: {
      suggestions,
      todoList,
    },
  });
}

describe("todolist", () => {
  it("is initially invalid", () => {
    expect(todolist([]).valid).toBe(false);
  });

  it("becomes valid for sufficient long todo name", () => {
    const vm = todolist([]);
    vm.newTodo = "1234";
    expect(vm.valid).toBe(true);
  });

  it("adds valid new todo", () => {
    const vm = todolist([]);
    vm.newTodo = "1234";
    vm.addNewTodo();
    expect(vm.todos.length).toBe(1);
    const todo = vm.todos[0];
    expect(todo.title).toBe("1234");
    expect(todo.completed).toBe(false);
  });

  it("ignores invalid new todo", () => {
    const vm = todolist([]);
    vm.newTodo = "123";
    vm.addNewTodo();
    expect(vm.todos.length).toBe(0);
  });

  it("filters out a suggestion", () => {
    const vm = todolist([{
      completed: false,
      title: "  wash  the car ",
    }], ["Feed the dog", " Wash the  car"]);
    expect(vm.openSuggestions.length).toBe(1);
    expect(vm.openSuggestions[0]).toBe("Feed the dog");
  });
});
