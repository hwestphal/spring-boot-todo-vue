import "bootstrap/dist/css/bootstrap.css";
import "../css/styles.css";

import Vue from "vue";
import VueI18n from "vue-i18n";

Vue.use(VueI18n);

import Autocomplete from "./autocomplete.vue";

Vue.component("auto-complete", Autocomplete);

import Todolist, { ITodo } from "./todolist.vue";

export function main(
    todoList: ITodo[],
    suggestions: string[],
    action: string,
    el: string,
    locale: string,
    messages: VueI18n.LocaleMessageObject) {

    return new Vue({
        el,
        i18n: new VueI18n({ locale, messages: { [locale]: messages } }),

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
