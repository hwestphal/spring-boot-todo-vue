import { Todo } from "@Generated/openapi";
import Vue from "vue";
import { Component, Inject } from "vue-property-decorator";
import AutoComplete from "./AutoComplete.vue";
import { Button, ButtonGroup, Col, MessageBox, Row } from "./elements";
import { Suggestions } from "./state/suggestions";
import { ConflictError, TodoList } from "./state/todoList";

@Component({
    components: {
        AutoComplete,
        [Button.name]: Button,
        [ButtonGroup.name]: ButtonGroup,
        [Col.name]: Col,
        [Row.name]: Row,
    },
})
export default class App extends Vue {
    newTodo = "";

    @Inject(TodoList.name)
    private todoList!: TodoList;

    @Inject(Suggestions.name)
    private suggestions!: Suggestions;

    get openTodos() {
        return this.todoList.todos.filter((t) => !t.completed);
    }

    get doneTodos() {
        return this.todoList.todos.filter((t) => t.completed);
    }

    get valid() {
        return this.newTodo.length > 3;
    }

    get openSuggestions() {
        return this.suggestions.openSuggestions;
    }

    get changed() {
        return this.todoList.changed;
    }

    close(todo: Todo) {
        todo.completed = true;
    }

    open(todo: Todo) {
        todo.completed = false;
    }

    remove(todo: Todo) {
        this.todoList.remove(todo);
    }

    closeAll() {
        this.todoList.todos.forEach((todo) => {
            todo.completed = true;
        });
    }

    addNewTodo() {
        if (this.valid) {
            this.todoList.add(this.newTodo);
            this.newTodo = "";
        }
    }

    reset() {
        this.todoList.reset();
    }

    async save() {
        try {
            await this.todoList.save();
        } catch (error) {
            if (error instanceof ConflictError) {
                try {
                    await MessageBox.confirm(this.$t("concurrentModification.message").toString(),
                        this.$t("concurrentModification.title").toString(), { type: "error" });
                    await this.todoList.refresh();
                } catch (result) {
                }
            } else {
                throw error;
            }
        }
    }
}
