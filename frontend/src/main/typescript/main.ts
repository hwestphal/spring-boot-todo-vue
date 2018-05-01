import "../css/global.scss";

import Vue from "vue";
import VueI18n from "vue-i18n";
import { ITodo } from "./todolist";
import Todolist from "./todolist.vue";

Vue.use(VueI18n);

export = (
    todoList: ITodo[],
    suggestions: string[],
    action: string,
    el: string,
    locale: string,
    messages: VueI18n.LocaleMessageObject) => new Vue({
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
