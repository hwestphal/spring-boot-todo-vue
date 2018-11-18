import "../css/global.scss";

import { TodoListApi } from "@Generated/openapi";
import Vue from "vue";
import VueI18n from "vue-i18n";
import App from "./App.vue";
import { configureLocale } from "./elements";
import { Suggestions } from "./state/suggestions";
import { TodoList } from "./state/todoList";

Vue.use(VueI18n);

export default (
    suggestions: string[],
    basePath: string,
    el: string,
    locale: string,
    messages: VueI18n.LocaleMessageObject) => {

    function main() {
        configureLocale(locale);

        const todoList = new TodoList(new TodoListApi({ basePath }));

        return new Vue({
            el,
            i18n: new VueI18n({ locale, messages: { [locale]: messages } }),
            provide: {
                [Suggestions.name]: new Suggestions(suggestions, todoList),
                [TodoList.name]: todoList,
            },
            render: (h) => h(App),
        });
    }

    /* istanbul ignore else */
    if (window.fetch) {
        main();
    } else {
        const src = (document.querySelector("script[src]") as HTMLScriptElement).src;
        const script = document.createElement("script");
        script.src = src + "/../polyfills.js";
        script.onload = main;
        document.head!.appendChild(script);
    }
};
