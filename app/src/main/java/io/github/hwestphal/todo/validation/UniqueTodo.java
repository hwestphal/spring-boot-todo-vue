package io.github.hwestphal.todo.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Constraint(validatedBy = UniqueTodoValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueTodo {

    String message() default "{io.github.hwestphal.todo.validation.UniqueTodo.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
