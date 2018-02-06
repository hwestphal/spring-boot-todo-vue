<template>
    <div class="row">
        <div class="col-md-6">
            <div class="todolist">
                <h1>Todos</h1>
                <div class="form-group" :class="{ 'has-error': !valid }">
                    <auto-complete class="form-control" placeholder="Add todo" :list="openSuggestions" v-model="newTodo" @enter="addNewTodo"></auto-complete>
                </div>
                <div class="btn-group">
                    <button class="btn btn-primary" @click="closeAll" :disabled="!openTodos.length">Mark all as done</button>
                    <button class="btn btn-primary" @click="reset" :disabled="!changed">Reset</button>
                    <button class="btn btn-primary" @click="save" :disabled="!changed">Save</button>
                </div>
                <hr>
                <ul class="items list-unstyled">
                    <li v-for="todo in openTodos" :key="todo.id">{{ todo.title }}
                        <button class="btn btn-default btn-xs pull-right" @click="close(todo)">
                            <span class="glyphicon glyphicon-ok"></span>
                        </button>
                    </li>
                </ul>
                <div class="todo-footer">
                    <strong>{{ openTodos.length }}</strong> Items Left
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="todolist">
                <h1>Already Done</h1>
                <ul class="items list-unstyled">
                    <li class="done" v-for="todo in doneTodos" :key="todo.id">{{ todo.title }}
                        <div class="btn-group pull-right">
                            <button class="btn btn-default btn-xs" @click="open(todo)">
                                <span class="glyphicon glyphicon-arrow-left"></span>
                            </button>
                            <button class="btn btn-default btn-xs" @click="remove(todo)">
                                <span class="glyphicon glyphicon-remove"></span>
                            </button>
                        </div>
                    </li>
                </ul>
            </div>
        </div>
        <form ref="form" :action="action" method="post">
            <input type="hidden" name="todos" :value="todos | json">
        </form>
    </div>
</template>

<script lang="ts" src="./todolist.ts"></script>
