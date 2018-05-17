package io.github.hwestphal.mvc;

import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class JsonRequestParamResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;

    public JsonRequestParamResolver(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        JavaType type = objectMapper.getTypeFactory().constructType(parameter.getGenericParameterType());
        ObjectReader reader = objectMapper.readerFor(type);
        JsonRequestParam annotation = parameter.getParameterAnnotation(JsonRequestParam.class);
        if (annotation == null) {
            // won't happen due to check in "supportsParameter" method, but needed for SpotBugs
            return new Error();
        }
        String name = annotation.name();
        String value = webRequest.getParameter(name);
        if (value == null || value.isEmpty()) {
            if (annotation.required()) {
                throw new ServletRequestBindingException("Missing argument '" + name + "'");
            }
            return null;
        }
        return reader.readValue(value);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(JsonRequestParam.class) != null;
    }

}
