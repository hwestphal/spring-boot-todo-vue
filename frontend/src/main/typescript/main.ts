import "../css/global.scss";

import Vue from "vue";
import VueI18n from "vue-i18n";
import Todolist from "./todolist.vue";

Vue.use(VueI18n);

export = (
    suggestions: string[],
    basePath: string,
    el: string,
    locale: string,
    messages: VueI18n.LocaleMessageObject) => new Vue({
        el,
        i18n: new VueI18n({ locale, messages: { [locale]: messages } }),
        render: (h) => h(Todolist, {
            props: {
                basePath,
                suggestions,
            },
        }),
    });
