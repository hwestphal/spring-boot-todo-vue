package io.github.hwestphal.todo;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
@SpringBootApplication
@EnableJpaAuditing
public class TodoListApplication {

    private final TodoRepository todoRepository;
    private final ObjectReader todosReader;

    @Autowired
    public TodoListApplication(TodoRepository todoRepository, ObjectMapper objectMapper) {
        this.todoRepository = todoRepository;
        this.todosReader = objectMapper.readerFor(new TypeReference<List<Todo>>() {
        });
    }

    @RequestMapping(produces = MediaType.TEXT_HTML_VALUE, method = { RequestMethod.GET, RequestMethod.HEAD })
    public ModelAndView todosAsHtml() {
        return new ModelAndView("index", "todos", todos());
    }

    @RequestMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, method = RequestMethod.POST)
    @Transactional
    public String saveTodos(@RequestParam("todos") String todosAsJson) throws JsonProcessingException, IOException {
        putTodos(todosReader.readValue(todosAsJson));
        return "redirect:/";
    }

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.HEAD })
    @ResponseBody
    public List<Todo> todos() {
        return todoRepository.findAllByOrderByIdAsc();
    }

    @RequestMapping(path = "{id}", method = { RequestMethod.GET, RequestMethod.HEAD })
    public ResponseEntity<Todo> todo(@PathVariable("id") long id) {
        return todoRepository.findOne(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> todos(@RequestBody Todo todo) {
        todo.setId(null);
        todo.setVersion(null);
        todo = todoRepository.save(todo);
        return ResponseEntity.created(linkTo(methodOn(TodoListApplication.class).todo(todo.getId())).toUri()).build();
    }

    @RequestMapping(path = "{id}", method = RequestMethod.PUT)
    @Transactional
    public ResponseEntity<?> putTodo(@PathVariable("id") long id, @RequestBody Todo todo) {
        return todoRepository.findByIdAndVersion(id, todo.getVersion()).map(foundTodo -> {
            foundTodo.setTitle(todo.getTitle());
            foundTodo.setCompleted(todo.isCompleted());
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    @Transactional
    public void putTodos(@RequestBody List<Todo> todos) {
        Map<Long, Todo> allTodos = todoRepository.findAll().stream().collect(
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
            } else {
                todo.setId(null);
                todo.setVersion(null);
                todoRepository.save(todo);
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
