package io.github.hwestphal.todo;

import static io.github.hwestphal.todo.generated.tables.Todo.TODO;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.hwestphal.todo.validation.UniqueTodo;

import org.jooq.impl.DSL;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Transactional
@Validated
public class TodoListService {

    private final TodoRepository todoRepository;

    public TodoListService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<io.github.hwestphal.todo.api.generated.Todo> getTodos() {
        return todoRepository.findAll(DSL.noCondition()).stream().map(TodoListService::toApi).collect(Collectors.toList());
    }

    public long addTodo(@UniqueTodo io.github.hwestphal.todo.api.generated.Todo todo) {
        return todoRepository.insert(fromApi(todo));
    }

    public io.github.hwestphal.todo.api.generated.Todo getTodo(long id) {
        Todo todo = todoRepository.findOne(TODO.ID.eq(id));
        if (todo == null) {
            return null;
        }
        return toApi(todo);
    }

    public boolean updateTodo(long id, io.github.hwestphal.todo.api.generated.Todo todo) {
        Todo foundTodo = todoRepository.findOneForUpdate(TODO.ID.eq(id).and(TODO.VERSION.eq(todo.getVersion())));
        if (foundTodo == null) {
            return false;
        }
        foundTodo.setTitle(todo.getTitle());
        foundTodo.setCompleted(todo.getCompleted());
        todoRepository.update(foundTodo);
        return true;
    }

    public void overwriteTodos(List<io.github.hwestphal.todo.api.generated.Todo> todos)
            throws OptimisticLockingFailureException {
        Map<Long, Todo> allTodos = todoRepository.findAllForUpdate(DSL.noCondition())
                .stream()
                .collect(Collectors.toMap(Todo::getId, Function.identity()));
        for (io.github.hwestphal.todo.api.generated.Todo todo : todos) {
            Todo updatedTodo = allTodos.remove(todo.getId());
            if (updatedTodo != null) {
                if (!updatedTodo.getVersion().equals(todo.getVersion())) {
                    throw new OptimisticLockingFailureException(
                            String.format("cannot update %s with stale data %s", updatedTodo, todo));
                }
                updatedTodo.setTitle(todo.getTitle());
                updatedTodo.setCompleted(todo.getCompleted());
                todoRepository.update(updatedTodo);
            } else {
                todoRepository.insert(fromApi(todo));
            }
        }
        todoRepository.deleteAll(TODO.ID.in(allTodos.keySet()));
    }

    public boolean deleteTodo(long id) {
        return todoRepository.deleteAll(TODO.ID.eq(id)) > 0;
    }

    public void deleteTodos() {
        todoRepository.deleteAll(DSL.noCondition());
    }

    private static Todo fromApi(io.github.hwestphal.todo.api.generated.Todo todo) {
        return Todo.builder()
                .id(todo.getId())
                .version(todo.getVersion())
                .title(todo.getTitle())
                .completed(todo.getCompleted())
                .build();
    }

    private static io.github.hwestphal.todo.api.generated.Todo toApi(Todo todo) {
        return new io.github.hwestphal.todo.api.generated.Todo().id(todo.getId())
                .version(todo.getVersion())
                .title(todo.getTitle())
                .completed(todo.isCompleted());
    }

}
