/* globals describe: false */
/* globals it: false */
/* globals expect: false */

define([ 'todolist' ], function(todolist) {
  describe('todolist', function() {
    it('is initially invalid', function() {
      expect(todolist([]).valid).toBe(false);
    });

    it('becomes valid for sufficient long todo name', function() {
      var vm = todolist([]);
      vm.newTodo = '1234';
      expect(vm.valid).toBe(true);
    });

    it('adds valid new todo', function() {
      var vm = todolist([]), todo;
      vm.newTodo = '1234';
      vm.addNewTodo();
      expect(vm.todos.length).toBe(1);
      todo = vm.todos[0];
      expect(todo.title).toBe('1234');
      expect(todo.completed).toBe(false);
    });

    it('ignores invalid new todo', function() {
      var vm = todolist([]);
      vm.newTodo = '123';
      vm.addNewTodo();
      expect(vm.todos.length).toBe(0);
    });

    it('filters out a suggestion', function() {
      var vm = todolist([ {
        title : '  wash  the car '
      } ], [ 'Feed the dog', ' Wash the  car' ]);
      expect(vm.openSuggestions.length).toBe(1);
      expect(vm.openSuggestions[0]).toBe('Feed the dog');
    });

  });
});
