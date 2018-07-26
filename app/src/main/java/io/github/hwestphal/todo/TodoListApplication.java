package io.github.hwestphal.todo;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.hwestphal.auditing.EnableAuditing;
import io.github.hwestphal.i18n.MessageSourceConfiguration;
import io.github.hwestphal.mvc.JsonRequestParam;
import io.github.hwestphal.todo.api.generated.TodoListApi;
import io.github.hwestphal.todo.api.generated.Todos;
import io.github.hwestphal.todo.generated.QTodo;

import com.querydsl.core.types.dsl.Expressions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
@SpringBootApplication
@EnableAuditing
@Import(MessageSourceConfiguration.class)
public class TodoListApplication implements TodoListApi {

    private final TodoRepository todoRepository;
    private final TransactionTemplate txTemplate;

    public TodoListApplication(TodoRepository todoRepository, TransactionTemplate txTemplate) {
        this.todoRepository = todoRepository;
        this.txTemplate = txTemplate;
    }

    @GetMapping("/openapi")
    public String swaggerUi() {
        return "swagger-ui";
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public ModelAndView todosAsHtml() {
        return new ModelAndView("index", "todos", todos().getBody());
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String saveTodos(@JsonRequestParam(name = "todos") List<io.github.hwestphal.todo.api.generated.Todo> todos) {
        overwriteTodos(todos);
        return "redirect:/";
    }

    @Override
    public ResponseEntity<Todos> todos() {
        return txTemplate.execute((tx) -> {
            Todos todos = new Todos();
            for (Todo todo : todoRepository.findAll(Expressions.TRUE)) {
                todos.add(
                        new io.github.hwestphal.todo.api.generated.Todo().id(todo.getId())
                                .version(todo.getVersion())
                                .title(todo.getTitle())
                                .completed(todo.isCompleted()));
            }
            return ResponseEntity.ok(todos);
        });
    }

    @Override
    public ResponseEntity<Void> addTodo(io.github.hwestphal.todo.api.generated.Todo todo) {
        return txTemplate.execute((tx) -> {
            long id = todoRepository.insert(Todo.builder().title(todo.getTitle()).completed(todo.getCompleted()).build());
            return ResponseEntity.created(linkTo(methodOn(TodoListApplication.class)._todo(id)).toUri()).build();
        });
    }

    @Override
    public ResponseEntity<io.github.hwestphal.todo.api.generated.Todo> todo(Long id) {
        return txTemplate.execute((tx) -> {
            Todo todo = todoRepository.findOne(QTodo.todo.id.eq(id));
            if (todo == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(
                    new io.github.hwestphal.todo.api.generated.Todo().id(todo.getId())
                            .version(todo.getVersion())
                            .title(todo.getTitle())
                            .completed(todo.isCompleted()));
        });
    }

    @Override
    public ResponseEntity<Void> updateTodo(Long id, io.github.hwestphal.todo.api.generated.Todo todo) {
        return txTemplate.execute((tx) -> {
            QTodo q = QTodo.todo;
            Todo foundTodo = todoRepository.findOneForUpdate(q.id.eq(id).and(q.version.eq(todo.getVersion())));
            if (foundTodo == null) {
                return ResponseEntity.notFound().build();
            }
            foundTodo.setTitle(todo.getTitle());
            foundTodo.setCompleted(todo.getCompleted());
            todoRepository.update(foundTodo);
            return ResponseEntity.ok().build();
        });
    }

    @Override
    public ResponseEntity<Void> overwriteTodos(List<io.github.hwestphal.todo.api.generated.Todo> todos) {
        return txTemplate.execute((tx) -> {
            Map<Long, Todo> allTodos = todoRepository.findAllForUpdate(Expressions.TRUE).stream().collect(
                    Collectors.toMap(Todo::getId, Function.identity()));
            for (io.github.hwestphal.todo.api.generated.Todo todo : todos) {
                Todo updatedTodo = allTodos.remove(todo.getId());
                if (updatedTodo != null) {
                    if (!updatedTodo.getVersion().equals(todo.getVersion())) {
                        throw new OptimisticLockingFailureException(
                                String.format("cannot update %s with stale data %s", updatedTodo, todo));
                    }
                    updatedTodo.setTitle(todo.getTitle());
                    updatedTodo.setCompleted(todo.getCompleted());
                    todoRepository.update(updatedTodo);
                } else {
                    todoRepository.insert(Todo.builder().title(todo.getTitle()).completed(todo.getCompleted()).build());
                }
            }
            todoRepository.deleteAll(QTodo.todo.id.in(allTodos.keySet()));
            return ResponseEntity.ok().build();
        });
    }

    @Override
    public ResponseEntity<Void> deleteTodo(Long id) {
        return txTemplate.execute((tx) -> {
            if (todoRepository.deleteAll(QTodo.todo.id.eq(id)) > 0) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        });
    }

    @Override
    public ResponseEntity<Void> deleteTodos() {
        return txTemplate.execute((tx) -> {
            todoRepository.deleteAll(Expressions.TRUE);
            return ResponseEntity.ok().build();
        });
    }

    public static void main(String[] args) {
        SpringApplication.run(TodoListApplication.class, args);
    }

}
