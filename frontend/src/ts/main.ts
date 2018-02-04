import "./autocomplete";
import { default as todolist, ITodo } from "./todolist";

export function main(todos: ITodo[]) {
  todolist(todos,
    ["Wash the car",
      "Learn Typescript",
      "Get a life",
      "Feed the dog",
      "Feed the cat"],
    "#todolist-template",
    "#todolist");
}
