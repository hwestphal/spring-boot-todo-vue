import "awesomplete/awesomplete.css";
import "bootstrap/dist/css/bootstrap.css";
import "../css/styles.css";

import Vue from "vue";
import Autocomplete from "./autocomplete.vue";
import { ITodo } from "./todolist";
import Todolist from "./todolist.vue";

export function main(todos: ITodo[], action: string, el: string) {
  Vue.component("auto-complete", Autocomplete);
  return new Vue({
    el,
    render(createElement) {
      return createElement(Todolist, {
        props: {
          action,
          suggestions: ["Wash the car",
            "Learn Typescript",
            "Get a life",
            "Feed the dog",
            "Feed the cat"],
          todoList: todos,
        },
      });
    },
  });
}
