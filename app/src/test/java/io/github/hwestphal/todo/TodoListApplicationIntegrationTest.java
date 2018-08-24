package io.github.hwestphal.todo;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.github.hwestphal.todo.api.generated.BadRequestDetails;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = "logging.level.com.querydsl.sql=DEBUG")
@AutoConfigureTestDatabase
public class TodoListApplicationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

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
    public void shouldOverwriteAndRemoveAllTodos() {
        Todo todo1 = Todo.builder().title("todo1").build();
        todo1 = restTemplate.getForObject(restTemplate.postForLocation("/", todo1), Todo.class);
        Todo todo2 = Todo.builder().title("todo2").build();
        restTemplate.postForLocation("/", todo2);
        List<?> todos = restTemplate.getForObject("/", List.class);
        assertThat(todos).hasSize(2);

        Todo todo1a = Todo.builder().id(todo1.getId()).version(todo1.getVersion()).title("todo1a").build();
        Todo todo3 = Todo.builder().title("todo3").completed(true).build();
        restTemplate.put("/", Arrays.asList(todo3, todo1a));
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

    @Test
    public void shouldReturnBadRequestDetailsForInvalidPost() {
        shouldReturnBadRequestDetails(HttpMethod.POST, Todo.builder().build(), "todo.title");
    }

    @Test
    public void shouldReturnBadRequestDetailsForInvalidPut() {
        shouldReturnBadRequestDetails(
                HttpMethod.PUT,
                Arrays.asList(
                        Collections.emptyMap(),
                        Todo.builder().title("123").build(),
                        Collections.singletonMap("title", "1234")),
                "todo[0].title",
                "todo[0].completed",
                "todo[1].title",
                "todo[2].completed");
    }

    private void shouldReturnBadRequestDetails(HttpMethod method, Object payload, String... path) {
        ResponseEntity<List<BadRequestDetails>> response = restTemplate
                .exchange("/", method, new HttpEntity<>(payload), new ParameterizedTypeReference<List<BadRequestDetails>>() {
                });
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        List<BadRequestDetails> details = response.getBody();
        assertThat(details).hasSize(path.length);
        assertThat(details.stream().map(d -> d.getPath())).containsExactlyInAnyOrder(path);
    }

    @Test
    public void shouldReturn409OnOptimisticLockingFailure() {
        Todo todo = Todo.builder().title("todo").build();
        todo = restTemplate.getForObject(restTemplate.postForLocation("/", todo), Todo.class);
        todo.setVersion(todo.getVersion() + 1);
        ResponseEntity<String> response = restTemplate
                .exchange("/", HttpMethod.PUT, new HttpEntity<>(Collections.singletonList(todo)), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotEmpty();
    }

}
