import Awesomplete from "awesomplete";
import Vue from "vue";
import { Component, Emit, Prop, Watch } from "vue-property-decorator";

@Component
export default class Autocomplete extends Vue {
    @Prop()
    readonly value!: string;
    @Prop()
    readonly list!: string[];

    private awesomplete!: Awesomplete;

    mounted() {
        const el = this.$el;
        this.awesomplete = new Awesomplete(el, {
            list: this.list,
        });
        el.addEventListener("awesomplete-selectcomplete", (event) => {
            this.input((event.target as HTMLInputElement).value);
            this.enter();
        });
    }

    @Emit()
    input(value: string) {
    }

    @Emit()
    enter() {
    }

    @Watch("list")
    onListChanged(list: string[]) {
        this.awesomplete.list = list;
    }
}
