package io.github.hwestphal.todo;

import java.util.Iterator;

import javax.validation.ConstraintViolation;
import javax.validation.ElementKind;
import javax.validation.Path;
import javax.validation.Path.Node;

import io.github.hwestphal.todo.api.generated.BadRequestDetails;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;

@Component
public class ConstraintViolationMapper {

    public BadRequestDetails mapToBadRequestDetails(ConstraintViolation<?> violation, MethodParameter parameter) {
        BadRequestDetails details = new BadRequestDetails();
        details.setPath(createStringPath(violation.getPropertyPath(), parameter));
        details.setMessage(violation.getMessage());
        details.setRejectedValue(violation.getInvalidValue());
        return details;
    }

    private String createStringPath(Path path, MethodParameter parameter) {
        StringBuilder builder = new StringBuilder();
        Iterator<Node> it = path.iterator();
        boolean first = true;
        if (parameter != null) {
            // prepend parameter name
            String parameterName = parameter.getParameterName();
            if (parameterName == null) {
                builder.append('[');
                builder.append(parameter.getParameterIndex());
                builder.append(']');
            } else {
                builder.append(parameterName);
            }
            first = false;
        } else {
            // skip method name
            Node firstNode = it.next();
            if (firstNode.getKind() != ElementKind.METHOD) {
                it = path.iterator();
            }
        }
        while (it.hasNext()) {
            Node node = it.next();
            String name = node.toString();
            if (name.isEmpty()) {
                continue;
            }
            if (!first) {
                builder.append('.');
            }
            builder.append(name);
            first = false;
        }
        return builder.toString();
    }

}
