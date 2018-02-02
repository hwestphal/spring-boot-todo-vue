define([ 'vue', 'awesomplete' ], function(Vue, Awesomplete) {
  Vue.component('auto-complete', {
    template : '<input type="text" :value="value" @input="update" @keyup.enter="enter">',
    props : [ 'value', 'list' ],

    mounted : function() {
      var el = this.$el, that = this;
      this.awesomeplete = new Awesomplete(el, {
        list : this.list
      });
      el.addEventListener('awesomplete-selectcomplete', function() {
        that.update();
        that.enter();
      });
    },

    watch : {
      list : function(list) {
        this.awesomeplete.list = list;
      }
    },

    methods : {
      update : function() {
        this.$emit('input', this.$el.value);
      },
      enter : function() {
        this.$emit('enter');
      }
    }
  });
});
