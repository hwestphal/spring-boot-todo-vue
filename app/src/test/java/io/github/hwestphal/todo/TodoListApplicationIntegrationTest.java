package io.github.hwestphal.todo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "logging.level.com.querydsl.sql=DEBUG")
@AutoConfigureTestDatabase
public class TodoListApplicationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldAddAndChangeAndRemoveTodo() {
        Todo todo = Todo.builder().title("todo1").build();

        URI uri = restTemplate.postForLocation("/", todo);
        Todo foundTodo = restTemplate.getForObject(uri, Todo.class);
        assertThat(foundTodo.getTitle()).isEqualTo(todo.getTitle());
        assertThat(foundTodo.isCompleted()).isFalse();

        todo.setTitle("todo2");
        todo.setCompleted(true);
        todo.setVersion(foundTodo.getVersion());
        restTemplate.put(uri, todo);
        foundTodo = restTemplate.getForObject(uri, Todo.class);
        assertThat(foundTodo.getTitle()).isEqualTo(todo.getTitle());
        assertThat(foundTodo.isCompleted()).isTrue();

        restTemplate.delete(uri);

        assertThat(restTemplate.getForEntity(uri, Void.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void shouldOverwriteAndRemoveAllTodosWithPut() {
        shouldOverwriteAndRemoveAllTodos(ts -> restTemplate.put("/", ts));
    }

    @Test
    public void shouldOverwriteAndRemoveAllTodosWithPost() {
        shouldOverwriteAndRemoveAllTodos(ts -> restTemplate.postForEntity("/", asJsonFormField("todos", ts), Void.class));
    }

    private HttpEntity<?> asJsonFormField(String fieldName, Object obj) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        try {
            map.add(fieldName, objectMapper.writeValueAsString(obj));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
        return new HttpEntity<MultiValueMap<String, String>>(map, headers);
    }

    private void shouldOverwriteAndRemoveAllTodos(Consumer<List<Todo>> todoUploader) {
        Todo todo1 = Todo.builder().title("todo1").build();
        todo1 = restTemplate.getForObject(restTemplate.postForLocation("/", todo1), Todo.class);
        Todo todo2 = Todo.builder().title("todo2").build();
        restTemplate.postForLocation("/", todo2);
        List<?> todos = restTemplate.getForObject("/", List.class);
        assertThat(todos).hasSize(2);

        Todo todo1a = Todo.builder().id(todo1.getId()).version(todo1.getVersion()).title("todo1a").build();
        Todo todo3 = Todo.builder().title("todo3").completed(true).build();
        todoUploader.accept(Arrays.asList(todo3, todo1a));
        todos = restTemplate.getForObject("/", List.class);
        assertThat(todos).hasSize(2);
        @SuppressWarnings("unchecked")
        Map<String, Object> foundTodo1 = (Map<String, Object>) todos.get(0);
        assertThat(foundTodo1).containsEntry("title", "todo1a");
        assertThat(foundTodo1).containsEntry("completed", false);
        @SuppressWarnings("unchecked")
        Map<String, Object> foundTodo2 = (Map<String, Object>) todos.get(1);
        assertThat(foundTodo2).containsEntry("title", "todo3");
        assertThat(foundTodo2).containsEntry("completed", true);

        restTemplate.delete("/");
        todos = restTemplate.getForObject("/", List.class);
        assertThat(todos).isEmpty();
    }

}
