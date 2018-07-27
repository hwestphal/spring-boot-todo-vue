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

    public List<Todo> getTodos() {
        return todoRepository.findAll(Expressions.TRUE);
    }

    public long addTodo(Todo todo) {
        return todoRepository.insert(todo);
    }

    public Todo getTodo(long id) {
        return todoRepository.findOne(QTodo.todo.id.eq(id));
    }

    public boolean updateTodo(long id, Todo todo) {
        QTodo q = QTodo.todo;
        Todo foundTodo = todoRepository.findOneForUpdate(q.id.eq(id).and(q.version.eq(todo.getVersion())));
        if (foundTodo == null) {
            return false;
        }
        foundTodo.setTitle(todo.getTitle());
        foundTodo.setCompleted(todo.isCompleted());
        todoRepository.update(foundTodo);
        return true;
    }

    public void overwriteTodos(List<Todo> todos) {
        Map<Long, Todo> allTodos = todoRepository.findAllForUpdate(Expressions.TRUE).stream().collect(
                Collectors.toMap(Todo::getId, Function.identity()));
        for (Todo todo : todos) {
            Todo updatedTodo = allTodos.remove(todo.getId());
            if (updatedTodo != null) {
                if (!updatedTodo.getVersion().equals(todo.getVersion())) {
                    throw new OptimisticLockingFailureException(
                            String.format("cannot update %s with stale data %s", updatedTodo, todo));
                }
                updatedTodo.setTitle(todo.getTitle());
                updatedTodo.setCompleted(todo.isCompleted());
                todoRepository.update(updatedTodo);
            } else {
                todoRepository.insert(todo);
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

}
