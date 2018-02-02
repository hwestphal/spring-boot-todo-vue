import { default as todolist, Todo } from './todolist';
import './autocomplete';

export default (todos: Todo[]) => {
  todolist(todos, ['Wash the car', 'Learn Typescript', 'Get a life', 'Feed the dog', 'Feed the cat'], '#todolist-template', '#todolist');
};
