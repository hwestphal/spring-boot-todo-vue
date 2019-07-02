package io.github.hwestphal.todo.validation;

import static io.github.hwestphal.todo.generated.tables.Todo.TODO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.github.hwestphal.todo.TodoRepository;
import io.github.hwestphal.todo.api.generated.Todo;

class UniqueTodoValidator implements ConstraintValidator<UniqueTodo, Todo> {

    private final TodoRepository todoRepository;

    public UniqueTodoValidator(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public boolean isValid(Todo value, ConstraintValidatorContext context) {
        return todoRepository.findAll(TODO.TITLE.equalIgnoreCase(value.getTitle())).isEmpty();
    }

}
