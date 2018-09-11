import "../css/global.scss";

import { TodoListApi } from "@Generated/openapi";
import Vue from "vue";
import VueI18n from "vue-i18n";
import { configureLocale } from "./elements";
import Todolist from "./todolist.vue";

Vue.use(VueI18n);

export default (
    suggestions: string[],
    basePath: string,
    el: string,
    locale: string,
    messages: VueI18n.LocaleMessageObject) => {
    configureLocale(locale);
    return new Vue({
        el,
        i18n: new VueI18n({ locale, messages: { [locale]: messages } }),
        provide: {
            [TodoListApi.name]: new TodoListApi(undefined, basePath, fetch),
        },
        render: (h) => h(Todolist, {
            props: {
                suggestions,
            },
        }),
    });
};
