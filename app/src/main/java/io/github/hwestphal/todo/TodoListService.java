package io.github.hwestphal.todo;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.hwestphal.todo.generated.QTodo;

import com.querydsl.core.types.dsl.Expressions;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TodoListService {

    private final TodoRepository todoRepository;

    public TodoListService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<io.github.hwestphal.todo.api.generated.Todo> getTodos() {
        return todoRepository.findAll(Expressions.TRUE).stream().map(TodoListService::toApi).collect(Collectors.toList());
    }

    public long addTodo(io.github.hwestphal.todo.api.generated.Todo todo) {
        return todoRepository.insert(fromApi(todo));
    }

    public io.github.hwestphal.todo.api.generated.Todo getTodo(long id) {
        Todo todo = todoRepository.findOne(QTodo.todo.id.eq(id));
        if (todo == null) {
            return null;
        }
        return toApi(todo);
    }

    public boolean updateTodo(long id, io.github.hwestphal.todo.api.generated.Todo todo) {
        QTodo q = QTodo.todo;
        Todo foundTodo = todoRepository.findOneForUpdate(q.id.eq(id).and(q.version.eq(todo.getVersion())));
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
        Map<Long, Todo> allTodos = todoRepository.findAllForUpdate(Expressions.TRUE)
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
        todoRepository.deleteAll(QTodo.todo.id.in(allTodos.keySet()));
    }

    public boolean deleteTodo(long id) {
        return todoRepository.deleteAll(QTodo.todo.id.eq(id)) > 0;
    }

    public void deleteTodos() {
        todoRepository.deleteAll(Expressions.TRUE);
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
