package io.github.hwestphal.todo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class TodoTest {

    @Test
    public void sameEntitiesAreEqual() {
        Todo todo = new Todo();
        assertThat(todo).isEqualTo(todo);
    }

    @Test
    public void entitiesWithNoIdsAreUnequal() {
        assertThat(new Todo()).isNotEqualTo(new Todo());
    }

    @Test
    public void entitiesWithSameIdsAreEqual() {
        Todo todo1 = new Todo();
        todo1.setId(1L);
        Todo todo2 = new Todo();
        todo2.setId(1L);
        assertThat(todo1).isEqualTo(todo2);
    }

    @Test
    public void entitiesWithDifferentIdsAreUnequal() {
        Todo todo1 = new Todo();
        todo1.setId(1L);
        Todo todo2 = new Todo();
        todo2.setId(2L);
        assertThat(todo1).isNotEqualTo(todo2);
    }

    @Test
    public void entitiesWithSameIdsAndDifferentVersionsAreEqual() {
        Todo todo1 = new Todo();
        todo1.setId(1L);
        todo1.setVersion(1L);
        Todo todo2 = new Todo();
        todo2.setId(1L);
        todo2.setVersion(2L);
        assertThat(todo1).isEqualTo(todo2);
    }

}
