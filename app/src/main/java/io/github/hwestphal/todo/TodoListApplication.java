package io.github.hwestphal.todo;

import io.github.hwestphal.auditing.EnableAuditing;
import io.github.hwestphal.error.CustomErrorMvcConfiguration;
import io.github.hwestphal.i18n.MessageSourceConfiguration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
@SpringBootApplication
@EnableAuditing
@Import({ MessageSourceConfiguration.class, CustomErrorMvcConfiguration.class })
public class TodoListApplication {

    @GetMapping("/api")
    public String defaultSwaggerUi() {
        return "redirect:/api/v1";
    }

    @GetMapping("/api/{version}")
    public ModelAndView swaggerUi(@PathVariable("version") String version) {
        return new ModelAndView("swagger-ui", "version", version);
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    public static void main(String[] args) {
        SpringApplication.run(TodoListApplication.class, args);
    }

}
