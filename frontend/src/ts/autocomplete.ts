import Vue from 'vue';
import Awesomplete = require('awesomplete');

Vue.component('auto-complete', {
  template: '<input type="text" :value="value" @input="update" @keyup.enter="enter">',
  props: ['value', 'list'],

  data() {
    return <{ awesomplete: Awesomplete }>{}
  },

  mounted() {
    const el = this.$el;
    this.awesomplete = new Awesomplete(el, {
      list: this.list
    });
    el.addEventListener('awesomplete-selectcomplete', () => {
      this.update();
      this.enter();
    });
  },

  watch: {
    list(list) {
      this.awesomplete.list = list;
    }
  },

  methods: {
    update() {
      this.$emit('input', (<HTMLInputElement>this.$el).value);
    },
    enter() {
      this.$emit('enter');
    }
  }
});
