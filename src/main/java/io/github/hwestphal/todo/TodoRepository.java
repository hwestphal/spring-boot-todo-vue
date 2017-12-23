package io.github.hwestphal.todo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.RepositoryDefinition;

@RepositoryDefinition(domainClass = Todo.class, idClass = Long.class)
public interface TodoRepository {

    Optional<Todo> findOne(long id);

    Optional<Todo> findByIdAndVersion(long id, Long version);

    List<Todo> findAllByOrderByIdAsc();

    List<Todo> findAll();

    int deleteById(long id);

    void deleteAll();

    Todo save(Todo todo);

}
