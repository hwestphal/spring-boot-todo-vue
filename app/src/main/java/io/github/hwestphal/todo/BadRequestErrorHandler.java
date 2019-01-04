package io.github.hwestphal.todo;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import io.github.hwestphal.todo.api.generated.BadRequestDetails;

import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@ControllerAdvice
public class BadRequestErrorHandler {

    private final ConstraintViolationMapper constraintViolationMapper;

    public BadRequestErrorHandler(ConstraintViolationMapper constraintViolationMapper) {
        this.constraintViolationMapper = constraintViolationMapper;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<BadRequestDetails>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Set<ConstraintViolation<?>> violations;
        try {
            violations = ex.getBindingResult()
                    .getAllErrors()
                    .stream()
                    .map(e -> (ConstraintViolation<?>) e.unwrap(ConstraintViolation.class))
                    .collect(Collectors.toSet());
        } catch (IllegalArgumentException e) {
            // no wrapped constraint violation
            return handleBadRequestException(ex);
        }
        return handleViolations(violations, ex.getParameter(), ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<BadRequestDetails>> handleConstraintViolationException(ConstraintViolationException ex) {
        return handleViolations(ex.getConstraintViolations(), null, ex);
    }

    private ResponseEntity<List<BadRequestDetails>> handleViolations(
            Set<ConstraintViolation<?>> violations,
            MethodParameter parameter,
            Exception ex) {
        if (violations != null && !violations.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(
                            violations.stream()
                                    .map(v -> constraintViolationMapper.mapToBadRequestDetails(v, parameter))
                                    .collect(Collectors.toList()));
        }
        return handleBadRequestException(ex);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            ServletRequestBindingException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestPartException.class,
            BindException.class })
    public ResponseEntity<List<BadRequestDetails>> handleBadRequestException(Exception ex) {
        BadRequestDetails details = new BadRequestDetails();
        details.setMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(Collections.singletonList(details));
    }

}
