package io.github.hwestphal.todo;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import io.github.hwestphal.todo.api.generated.TodoListApi;

import org.springframework.dao.OptimisticLockingFailureException;
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
    public ResponseEntity<List<io.github.hwestphal.todo.api.generated.Todo>> todos() {
        return ResponseEntity
                .ok(todoListService.getTodos().stream().map(TodoListController::toApi).collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<Void> addTodo(io.github.hwestphal.todo.api.generated.Todo todo) {
        long id = todoListService.addTodo(fromApi(todo));
        return ResponseEntity.created(linkTo(methodOn(TodoListController.class)._todo(id)).toUri()).build();
    }

    @Override
    public ResponseEntity<io.github.hwestphal.todo.api.generated.Todo> todo(Long id) {
        Todo todo = todoListService.getTodo(id);
        if (todo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toApi(todo));
    }

    @Override
    public ResponseEntity<Void> updateTodo(Long id, io.github.hwestphal.todo.api.generated.Todo todo) {
        if (todoListService.updateTodo(id, fromApi(todo))) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    public ResponseEntity<Void> overwriteTodos(List<io.github.hwestphal.todo.api.generated.Todo> todos) {
        todoListService.overwriteTodos(todos.stream().map(TodoListController::fromApi).collect(Collectors.toList()));
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

    private static Todo fromApi(io.github.hwestphal.todo.api.generated.Todo todo) {
        return Todo.builder()
                .id(todo.getId())
                .version(todo.getVersion())
                .title(todo.getTitle())
                .completed(todo.getCompleted())
                .build();
    }

    private static io.github.hwestphal.todo.api.generated.Todo toApi(Todo todo) {
        return new io.github.hwestphal.todo.api.generated.Todo().id(todo.getId())
                .version(todo.getVersion())
                .title(todo.getTitle())
                .completed(todo.isCompleted());
    }

}
