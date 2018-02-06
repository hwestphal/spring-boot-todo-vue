import Awesomplete = require("awesomplete");
import Vue from "vue";

export default Vue.extend({
  props: ["value", "list"],

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
