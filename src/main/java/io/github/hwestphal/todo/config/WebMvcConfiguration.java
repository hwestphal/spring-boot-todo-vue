package io.github.hwestphal.todo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Value("${resources.version:}")
    private String version;

    @Value("${resources.path-patterns:/images/**,/js/**,/css/**}")
    private String[] pathPatterns;

    @Value("${resources.cache:true}")
    private boolean cache;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!StringUtils.isEmpty(version)) {
            registry.addResourceHandler("/**")
                    .addResourceLocations("classpath:static/")
                    .setCachePeriod(cache ? Integer.MAX_VALUE : 0)
                    .resourceChain(true)
                    .addResolver(new RelaxedVersionResourceResolver().addFixedVersionStrategy(version, pathPatterns));
        }
    }

}
