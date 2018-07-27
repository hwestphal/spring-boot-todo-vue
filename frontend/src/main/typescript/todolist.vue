<template>
    <div>
        <el-row>
            <el-col :span="12">
                <div :class="$style.todolist">
                    <h1>{{ $t('todos') }}</h1>
                    <div class="el-input" :class="$style.input">
                        <auto-complete class="el-input__inner" :class="{ [$style.error]: !valid }" :placeholder="$t('addTodo')" :list="openSuggestions" v-model="newTodo" @enter="addNewTodo"></auto-complete>
                    </div>
                    <el-button-group>
                        <el-button type="primary" @click="closeAll" :disabled="!openTodos.length">{{ $t('markAllAsDone') }}</el-button>
                        <el-button type="primary" @click="reset" :disabled="!changed">{{ $t('reset') }}</el-button>
                        <el-button type="primary" @click="save" :disabled="!changed">{{ $t('save') }}</el-button>
                    </el-button-group>
                    <hr>
                    <ul :class="$style.items">
                        <li v-for="todo in openTodos" :key="todo.id">
                            <el-row type="flex" justify="space-between">
                                <el-col :span="22">{{ todo.title }}</el-col>
                                <el-col :span="2">
                                    <el-button size="mini" icon="el-icon-check" @click="close(todo)"></el-button>
                                </el-col>
                            </el-row>
                        </li>
                    </ul>
                    <div :class="$style.footer" v-html="$tc('itemsLeft', openTodos.length, [openTodos.length])"></div>
                </div>
            </el-col>
            <el-col :span="12">
                <div :class="$style.todolist">
                    <h1>{{ $t('alreadyDone') }}</h1>
                    <ul :class="$style.items">
                        <li :class="$style.done" v-for="todo in doneTodos" :key="todo.id">
                            <el-row type="flex" justify="space-between">
                                <el-col :span="20">{{ todo.title }}</el-col>
                                <el-col :span="4">
                                    <el-button-group>
                                        <el-button size="mini" icon="el-icon-back" @click="open(todo)"></el-button>
                                        <el-button size="mini" icon="el-icon-close" @click="remove(todo)"></el-button>
                                    </el-button-group>
                                </el-col>
                            </el-row>
                        </li>
                    </ul>
                </div>
            </el-col>
        </el-row>
    </div>
</template>

<script lang="ts">
export { default } from "./todolist";
</script>

<style lang="scss" module>
@import "../css/vars";

.todolist {
  padding: 20px 20px 10px 20px;
  margin-top: 30px;
  h1 {
    margin: 0;
    padding-bottom: 20px;
    text-align: center;
    font-size: #{$--font-size-large * 5/3};
  }
}

.input {
  padding-bottom: 10px;
}

.footer {
  background-color: $--color-success;
  margin: 0 -20px -10px -20px;
  padding: 10px 20px;
}

.items {
  padding-left: 0px;
  list-style: none;
  li {
    padding: 10px 0;
    border-bottom: 1px solid $--border-color-light;
    &:last-child {
      border-bottom: none;
    }
  }
}

.done {
  text-decoration: line-through;
}

.error:focus {
  border-color: $--color-danger;
}
</style>
