import Awesomplete = require("awesomplete");
import Vue from "vue";

Vue.component("auto-complete", {
  props: ["value", "list"],
  template: '<input type="text" :value="value" @input="update" @keyup.enter="enter">',

  data() {
    return {} as { awesomplete: Awesomplete };
  },

  mounted() {
    const el = this.$el;
    this.awesomplete = new Awesomplete(el, {
      list: this.list,
    });
    el.addEventListener("awesomplete-selectcomplete", () => {
      this.update();
      this.enter();
    });
  },

  watch: {
    list(list) {
      this.awesomplete.list = list;
    },
  },

  methods: {
    update() {
      this.$emit("input", (this.$el as HTMLInputElement).value);
    },
    enter() {
      this.$emit("enter");
    },
  },
});
