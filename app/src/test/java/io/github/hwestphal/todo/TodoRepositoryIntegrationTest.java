package io.github.hwestphal.todo;

import static io.github.hwestphal.todo.generated.tables.Todo.TODO;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, properties = "logging.level.org.jooq.tools.LoggerListener=DEBUG")
@AutoConfigureTestDatabase
@Transactional
public class TodoRepositoryIntegrationTest {

    @Autowired
    private TodoRepository repository;

    @Test
    public void shouldFindOne() {
        Todo todo = Todo.builder().title("title").build();
        long id = repository.insert(todo);
        Todo foundTodo = repository.findOne(TODO.ID.eq(id));
        assertThat(foundTodo).isNotNull();
        assertThat(foundTodo.getId()).isEqualTo(id);
        assertThat(foundTodo.getTitle()).isEqualTo(todo.getTitle());
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldRejectNullTitle() {
        repository.insert(Todo.builder().build());
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldRejectShortTitle() {
        repository.insert(Todo.builder().title("t").build());
    }

    @Test
    public void shouldDeleteOne() {
        Todo todo = Todo.builder().title("title").build();
        long id = repository.insert(todo);
        Condition condition = TODO.ID.eq(id);
        assertThat(repository.deleteAll(condition)).isEqualTo(1);
        assertThat(repository.findOne(condition)).isNull();
        assertThat(repository.deleteAll(condition)).isEqualTo(0);
    }

    @Test
    public void shouldFindAll() {
        Todo todo1 = Todo.builder().title("title1").build();
        long id1 = repository.insert(todo1);
        Todo todo2 = Todo.builder().title("title2").build();
        long id2 = repository.insert(todo2);
        assertThat(repository.findAll(DSL.noCondition()).stream().map(Todo::getId).collect(Collectors.toList()))
                .containsExactly(id1, id2);
    }

    @Test
    public void shouldDeleteAll() {
        repository.insert(Todo.builder().title("title1").build());
        repository.insert(Todo.builder().title("title2").build());
        assertThat(repository.deleteAll(DSL.noCondition())).isEqualTo(2);
        assertThat(repository.findAll(DSL.noCondition())).isEmpty();
    }

}
