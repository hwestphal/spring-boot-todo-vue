import Vue from 'vue';

export interface Todo {
  title: string;
  completed: boolean;
}

function clone<T>(obj: T): T {
  return JSON.parse(JSON.stringify(obj));
}

function normalize(s: string) {
  return s.trim().toLowerCase().split(/\s+/).join(' ');
}

export default (todos: Todo[], suggestions: string[] = [], template?: string, element?: string) => new Vue({
  template: template,
  el: element,

  data: {
    todos: clone(todos),
    newTodo: '',
    changed: false
  },

  filters: {
    json: JSON.stringify
  },

  computed: {
    openTodos(): Todo[] {
      return this.todos.filter(t => !t.completed);
    },

    doneTodos(): Todo[] {
      return this.todos.filter(t => t.completed);
    },

    valid(): boolean {
      return this.newTodo.length > 3;
    },

    openSuggestions(): string[] {
      return suggestions.filter(s => !this.todos.some(t => normalize(t.title) === normalize(s)));
    }
  },

  methods: {
    close(todo: Todo) {
      todo.completed = true;
      this.changed = true;
    },

    open(todo: Todo) {
      todo.completed = false;
      this.changed = true;
    },

    remove(todo: Todo) {
      this.todos.splice(this.todos.indexOf(todo), 1);
      this.changed = true;
    },

    closeAll() {
      this.todos.forEach(todo => {
        todo.completed = true;
      });
      this.changed = true;
    },

    addNewTodo() {
      if (this.valid) {
        this.todos.push({
          title: this.newTodo,
          completed: false
        });
        this.newTodo = '';
        this.changed = true;
      }
    },

    reset() {
      this.todos = clone(todos);
      this.changed = false;
    },

    save() {
      (<HTMLFormElement>this.$refs.form).submit();
    }
  }
});
