import Vue from "vue";
import { Component, Prop } from "vue-property-decorator";

export interface ITodo {
    title: string;
    completed: boolean;
}

function clone<T>(obj: T): T {
    return JSON.parse(JSON.stringify(obj));
}

function normalize(s: string) {
    return s.trim().toLowerCase().split(/\s+/).join(" ");
}

@Component({
    filters: {
        json: JSON.stringify,
    },
})
export default class Todolist extends Vue {
    @Prop()
    readonly action!: string;
    @Prop()
    readonly suggestions!: string[];
    @Prop()
    readonly todoList!: ITodo[];

    changed = false;
    newTodo = "";
    todos = clone(this.todoList);

    get openTodos() {
        return this.todos.filter((t) => !t.completed);
    }

    get doneTodos() {
        return this.todos.filter((t) => t.completed);
    }

    get valid() {
        return this.newTodo.length > 3;
    }

    get openSuggestions() {
        return this.suggestions.filter((s) => !this.todos.some((t) => normalize(t.title) === normalize(s)));
    }

    close(todo: ITodo) {
        todo.completed = true;
        this.changed = true;
    }

    open(todo: ITodo) {
        todo.completed = false;
        this.changed = true;
    }

    remove(todo: ITodo) {
        this.todos.splice(this.todos.indexOf(todo), 1);
        this.changed = true;
    }

    closeAll() {
        this.todos.forEach((todo) => {
            todo.completed = true;
        });
        this.changed = true;
    }

    addNewTodo() {
        if (this.valid) {
            this.todos.push({
                completed: false,
                title: this.newTodo,
            });
            this.newTodo = "";
            this.changed = true;
        }
    }

    reset() {
        this.todos = clone(this.todoList);
        this.changed = false;
    }

    save() {
        (this.$refs.form as HTMLFormElement).submit();
    }
}
