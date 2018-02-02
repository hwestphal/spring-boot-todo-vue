define([ 'vue' ], function(Vue) {
  function clone(obj) {
    return JSON.parse(JSON.stringify(obj));
  }

  function normalize(s) {
    return s.trim().toLowerCase().split(/\s+/).join(' ');
  }

  return function(todos, suggestions, template, element) {
    suggestions = suggestions || [];

    return new Vue({
      template : template,
      el : element,

      data : {
        todos : clone(todos),
        newTodo : '',
        changed : false
      },

      filters : {
        json : function(value) {
          return JSON.stringify(value);
        }
      },

      computed : {
        openTodos : function() {
          return this.todos.filter(function(i) {
            return !i.completed;
          });
        },

        doneTodos : function() {
          return this.todos.filter(function(i) {
            return i.completed;
          });
        },

        valid : function() {
          return this.newTodo.length > 3;
        },

        openSuggestions : function() {
          var todos = this.todos;
          return suggestions.filter(function(s) {
            return !todos.some(function(t) {
              return normalize(t.title) === normalize(s);
            });
          });
        }
      },

      methods : {
        close : function(todo) {
          todo.completed = true;
          this.changed = true;
        },

        open : function(todo) {
          todo.completed = false;
          this.changed = true;
        },

        remove : function(todo) {
          this.todos.splice(this.todos.indexOf(todo), 1);
          this.changed = true;
        },

        closeAll : function() {
          this.todos.forEach(function(todo) {
            todo.completed = true;
          });
          this.changed = true;
        },

        addNewTodo : function() {
          if (this.valid) {
            this.todos.push({
              title : this.newTodo,
              completed : false
            });
            this.newTodo = '';
            this.changed = true;
          }
        },

        reset : function() {
          this.todos = clone(todos);
          this.changed = false;
        },

        save : function() {
          this.$refs.form.submit();
        }
      }
    });
  };
});
