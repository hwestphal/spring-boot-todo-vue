package io.github.hwestphal.todo;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import io.github.hwestphal.auditing.EnableAuditing;
import io.github.hwestphal.error.CustomErrorMvcConfiguration;
import io.github.hwestphal.i18n.MessageSourceConfiguration;
import io.github.hwestphal.todo.api.generated.TodoListApi;
import io.github.hwestphal.todo.api.generated.Todos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@SpringBootApplication
@EnableAuditing
@Import({ MessageSourceConfiguration.class, CustomErrorMvcConfiguration.class })
public class TodoListApplication implements TodoListApi {

    private final TodoListService todoListService;

    public TodoListApplication(TodoListService todoListService) {
        this.todoListService = todoListService;
    }

    @GetMapping("/openapi")
    public String swaggerUi() {
        return "swagger-ui";
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "index";
    }

    @Override
    public ResponseEntity<Todos> todos() {
        Todos todos = new Todos();
        for (Todo todo : todoListService.getTodos()) {
            todos.add(toApi(todo));
        }
        return ResponseEntity.ok(todos);
    }

    @Override
    public ResponseEntity<Void> addTodo(io.github.hwestphal.todo.api.generated.Todo todo) {
        long id = todoListService.addTodo(fromApi(todo));
        return ResponseEntity.created(linkTo(methodOn(TodoListApplication.class)._todo(id)).toUri()).build();
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
        todoListService.overwriteTodos(todos.stream().map(TodoListApplication::fromApi).collect(Collectors.toList()));
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

    public static void main(String[] args) {
        SpringApplication.run(TodoListApplication.class, args);
    }

}
