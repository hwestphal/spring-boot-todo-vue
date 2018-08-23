package io.github.hwestphal.error;

import java.util.List;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
public class CustomErrorMvcConfiguration {

    @Bean
    public CustomErrorController customErrorController(
            ErrorAttributes errorAttributes,
            ServerProperties serverProperties,
            ObjectProvider<List<ErrorViewResolver>> errorViewResolversProvider) {
        return new CustomErrorController(
                errorAttributes,
                serverProperties.getError(),
                errorViewResolversProvider.getIfAvailable());
    }

}
