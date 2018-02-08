package io.github.hwestphal.todo;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.hwestphal.auditing.EnableAuditing;
import io.github.hwestphal.mvc.JsonRequestParam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
@SpringBootApplication
@EnableAuditing
public class TodoListApplication {

    private final TodoRepository todoRepository;

    public TodoListApplication(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = { RequestMethod.GET, RequestMethod.HEAD })
    public ModelAndView todosAsHtml() {
        return new ModelAndView("index", "todos", todos());
    }

    @RequestMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, method = RequestMethod.POST)
    @Transactional
    public String saveTodos(@JsonRequestParam(name = "todos") List<Todo> todos) {
        putTodos(todos);
        return "redirect:/";
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.HEAD })
    @ResponseBody
    public List<Todo> todos() {
        return todoRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> todos(@RequestBody Todo todo) {
        todoRepository.insert(todo);
        return ResponseEntity.created(linkTo(methodOn(TodoListApplication.class).todo(todo.getId())).toUri()).build();
    }

    @RequestMapping(path = "{id}", method = { RequestMethod.GET, RequestMethod.HEAD })
    public ResponseEntity<Todo> todo(@PathVariable("id") long id) {
        Todo todo = todoRepository.findById(id);
        if (todo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(todo);
    }

    @RequestMapping(path = "{id}", method = RequestMethod.PUT)
    @Transactional
    public ResponseEntity<?> putTodo(@PathVariable("id") long id, @RequestBody Todo todo) {
        Todo foundTodo = todoRepository.findByIdAndVersionForUpdate(id, todo.getVersion());
        if (foundTodo == null) {
            return ResponseEntity.notFound().build();
        }
        foundTodo.setTitle(todo.getTitle());
        foundTodo.setCompleted(todo.isCompleted());
        todoRepository.update(foundTodo);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    @Transactional
    public void putTodos(@RequestBody List<Todo> todos) {
        Map<Long, Todo> allTodos = todoRepository.findAllForUpdate().stream().collect(
                Collectors.toMap(Todo::getId, Function.identity()));
        for (Todo todo : todos) {
            Todo updatedTodo = allTodos.remove(todo.getId());
            if (updatedTodo != null) {
                if (!updatedTodo.getVersion().equals(todo.getVersion())) {
                    throw new OptimisticLockingFailureException(
                            String.format("cannot update %s with stale data %s", updatedTodo, todo));
                }
                updatedTodo.setTitle(todo.getTitle());
                updatedTodo.setCompleted(todo.isCompleted());
                todoRepository.update(updatedTodo);
            } else {
                todoRepository.insert(todo);
            }
        }
        allTodos.keySet().forEach(todoRepository::deleteById);
    }

    @RequestMapping(path = "{id}", method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<?> deleteTodo(@PathVariable("id") long id) {
        if (todoRepository.deleteById(id) > 0) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteTodos() {
        todoRepository.deleteAll();
    }

    public static void main(String[] args) {
        SpringApplication.run(TodoListApplication.class, args);
    }

}
