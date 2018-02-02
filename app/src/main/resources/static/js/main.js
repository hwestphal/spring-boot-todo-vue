define([ 'todolist', 'autocomplete' ], function(todolist) {
  return function(todos) {
    todolist(todos, [ 'Wash the car', 'Learn Javascript', 'Get a life', 'Feed the dog', 'Feed the cat' ], '#todolist-template', '#todolist');
  };
});
