package io.github.hwestphal.todo;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;

import io.github.hwestphal.todo.api.generated.Todo;
import io.github.hwestphal.todo.api.generated.TodoListApi;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1")
public class TodoListController implements TodoListApi {

    private final TodoListService todoListService;

    public TodoListController(TodoListService todoListService) {
        this.todoListService = todoListService;
    }

    @Override
    public ResponseEntity<List<Todo>> todos() {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(todoListService.getTodos());
    }

    @Override
    public ResponseEntity<Void> addTodo(Todo todo) {
        long id = todoListService.addTodo(todo);
        return ResponseEntity.created(linkTo(methodOn(TodoListController.class)._todo(id)).toUri()).build();
    }

    @Override
    public ResponseEntity<Todo> todo(Long id) {
        Todo todo = todoListService.getTodo(id);
        if (todo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(todo);
    }

    @Override
    public ResponseEntity<Void> updateTodo(Long id, Todo todo) {
        if (todoListService.updateTodo(id, todo)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> overwriteTodos(List<Todo> todos) {
        todoListService.overwriteTodos(todos);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteTodo(Long id) {
        if (todoListService.deleteTodo(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> deleteTodos() {
        todoListService.deleteTodos();
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<?> handleOptimisticLockingFailureException(OptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.TEXT_PLAIN).body(ex.getMessage());
    }

}
