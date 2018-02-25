package io.github.hwestphal.todo.config;

import javax.sql.DataSource;

import com.querydsl.sql.H2Templates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.spring.SpringConnectionProvider;
import com.querydsl.sql.spring.SpringExceptionTranslator;
import com.querydsl.sql.types.YesNoType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class QuerydslConfiguration {

    @Bean
    SQLQueryFactory queryFactory(DataSource dataSource) {
        SQLTemplates templates = H2Templates.builder().build();
        com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(templates);
        configuration.setExceptionTranslator(new SpringExceptionTranslator());
        configuration.register(new YesNoType());
        return new SQLQueryFactory(configuration, new SpringConnectionProvider(dataSource));
    }

}
