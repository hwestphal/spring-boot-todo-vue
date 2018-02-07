import "bootstrap/dist/css/bootstrap.css";
import "../css/styles.css";

import Vue from "vue";
import VueI18n from "vue-i18n";
import Autocomplete from "./autocomplete.vue";
import { ITodo } from "./todolist";
import Todolist from "./todolist.vue";

export function main(
  todoList: ITodo[],
  suggestions: string[],
  action: string,
  el: string,
  locale: string,
  localeMessages: VueI18n.LocaleMessages) {

  Vue.component("auto-complete", Autocomplete);
  Vue.use(VueI18n);

  const messages: VueI18n.LocaleMessages = {};
  messages[locale] = localeMessages;
  const i18n = new VueI18n({ locale, messages });

  return new Vue({
    el,
    i18n,
    render(createElement) {
      return createElement(Todolist, {
        props: {
          action,
          suggestions,
          todoList,
        },
      });
    },
  });
}
