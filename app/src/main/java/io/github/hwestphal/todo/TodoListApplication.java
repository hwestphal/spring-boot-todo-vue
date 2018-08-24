package io.github.hwestphal.todo;

import io.github.hwestphal.auditing.EnableAuditing;
import io.github.hwestphal.error.CustomErrorMvcConfiguration;
import io.github.hwestphal.i18n.MessageSourceConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@SpringBootApplication
@EnableAuditing
@Import({ MessageSourceConfiguration.class, CustomErrorMvcConfiguration.class })
public class TodoListApplication {

    @GetMapping("/openapi")
    public String swaggerUi() {
        return "swagger-ui";
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        return "index";
    }

    public static void main(String[] args) {
        SpringApplication.run(TodoListApplication.class, args);
    }

}
