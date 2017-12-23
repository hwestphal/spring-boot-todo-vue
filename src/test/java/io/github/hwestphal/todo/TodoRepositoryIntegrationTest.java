package io.github.hwestphal.todo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureJson
public class TodoRepositoryIntegrationTest {

    private static final String AUDIT_USER = "user";

    @Autowired
    private TodoRepository repository;

    @Autowired
    private TestEntityManager entityManager;

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
        todo = repository.save(todo);
        Todo foundTodo = repository.findOne(todo.getId()).get();
        assertThat(foundTodo).isSameAs(todo);
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldRejectNullTitle() {
        repository.save(new Todo());
        entityManager.flush();
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldRejectShortTitle() {
        Todo todo = new Todo();
        todo.setTitle("t");
        repository.save(todo);
        entityManager.flush();
    }

    @Test
    public void shouldDeleteOne() {
        Todo todo = new Todo();
        todo.setTitle("title");
        todo = repository.save(todo);
        assertThat(repository.deleteById(todo.getId())).isEqualTo(1);
        assertThat(repository.findOne(todo.getId()).isPresent()).isFalse();
        assertThat(repository.deleteById(todo.getId())).isEqualTo(0);
    }

    @Test
    public void shouldFindAll() {
        Todo todo1 = new Todo();
        todo1.setTitle("title1");
        todo1 = repository.save(todo1);
        Todo todo2 = new Todo();
        todo2.setTitle("title2");
        todo2 = repository.save(todo2);
        assertThat(repository.findAllByOrderByIdAsc()).containsExactly(todo1, todo2);
    }

    @Test
    public void shouldDeleteAll() {
        Todo todo1 = new Todo();
        todo1.setTitle("title1");
        todo1 = repository.save(todo1);
        Todo todo2 = new Todo();
        todo2.setTitle("title2");
        todo2 = repository.save(todo2);
        repository.deleteAll();
        assertThat(repository.findAllByOrderByIdAsc()).isEmpty();
    }

}
