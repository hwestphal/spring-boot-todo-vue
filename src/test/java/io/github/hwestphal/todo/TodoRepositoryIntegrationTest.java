package io.github.hwestphal.todo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.stream.Collectors;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@MybatisTest
@ImportAutoConfiguration(ValidationAutoConfiguration.class)
public class TodoRepositoryIntegrationTest {

    private static final String AUDIT_USER = "user";

    @Autowired
    private TodoRepository repository;

    @MockBean
    private AuditorAware<String> auditor;

    @Before
    public void setAuditUser() {
        when(auditor.getCurrentAuditor()).thenReturn(AUDIT_USER);
    }

    @Test
    public void shouldFindOne() {
        Todo todo = new Todo();
        todo.setTitle("title");
        repository.insert(todo);
        Todo foundTodo = repository.findById(todo.getId());
        assertThat(foundTodo).isNotNull();
        assertThat(foundTodo.getId()).isEqualTo(todo.getId());
        assertThat(foundTodo.getTitle()).isEqualTo(todo.getTitle());
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldRejectNullTitle() {
        repository.insert(new Todo());
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldRejectShortTitle() {
        Todo todo = new Todo();
        todo.setTitle("t");
        repository.insert(todo);
    }

    @Test
    public void shouldDeleteOne() {
        Todo todo = new Todo();
        todo.setTitle("title");
        repository.insert(todo);
        assertThat(repository.deleteById(todo.getId())).isEqualTo(1);
        assertThat(repository.findById(todo.getId())).isNull();
        assertThat(repository.deleteById(todo.getId())).isEqualTo(0);
    }

    @Test
    public void shouldFindAll() {
        Todo todo1 = new Todo();
        todo1.setTitle("title1");
        repository.insert(todo1);
        Todo todo2 = new Todo();
        todo2.setTitle("title2");
        repository.insert(todo2);
        assertThat(repository.findAll().stream().map(Todo::getId).collect(Collectors.toList()))
                .containsExactly(todo1.getId(), todo2.getId());
    }

    @Test
    public void shouldDeleteAll() {
        Todo todo1 = new Todo();
        todo1.setTitle("title1");
        repository.insert(todo1);
        Todo todo2 = new Todo();
        todo2.setTitle("title2");
        repository.insert(todo2);
        repository.deleteAll();
        assertThat(repository.findAll()).isEmpty();
    }

}
