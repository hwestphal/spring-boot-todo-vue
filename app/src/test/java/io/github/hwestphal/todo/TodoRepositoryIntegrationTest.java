package io.github.hwestphal.todo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import io.github.hwestphal.todo.generated.QTodo;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE, properties = "logging.level.com.querydsl.sql=DEBUG")
@AutoConfigureTestDatabase
@Transactional
public class TodoRepositoryIntegrationTest {

    @Autowired
    private TodoRepository repository;

    @Test
    public void shouldFindOne() {
        Todo todo = Todo.builder().title("title").build();
        long id = repository.insert(todo);
        Todo foundTodo = repository.findOne(QTodo.todo.id.eq(id));
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
        Predicate predicate = QTodo.todo.id.eq(id);
        assertThat(repository.deleteAll(predicate)).isEqualTo(1);
        assertThat(repository.findOne(predicate)).isNull();
        assertThat(repository.deleteAll(predicate)).isEqualTo(0);
    }

    @Test
    public void shouldFindAll() {
        Todo todo1 = Todo.builder().title("title1").build();
        long id1 = repository.insert(todo1);
        Todo todo2 = Todo.builder().title("title2").build();
        long id2 = repository.insert(todo2);
        assertThat(repository.findAll(Expressions.TRUE).stream().map(Todo::getId).collect(Collectors.toList()))
                .containsExactly(id1, id2);
    }

    @Test
    public void shouldDeleteAll() {
        repository.insert(Todo.builder().title("title1").build());
        repository.insert(Todo.builder().title("title2").build());
        assertThat(repository.deleteAll(Expressions.TRUE)).isEqualTo(2);
        assertThat(repository.findAll(Expressions.TRUE)).isEmpty();
    }

}
