package io.github.hwestphal.auditing;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.aop.Advisor;
import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mapping.context.PersistentEntities;

@Configuration
class AuditingConfiguration {

    @Autowired(required = false)
    private AuditorAware<?> auditorAware;

    @Bean
    @SuppressWarnings("serial")
    AbstractAdvisorAutoProxyCreator auditingPostProcessor() throws ClassNotFoundException {
        AuditingHandler auditingHandler;
        if (auditorAware != null) {
            auditingHandler = new AuditingHandler(new PersistentEntities(Collections.emptySet()));
            auditingHandler.setAuditorAware(auditorAware);
            auditingHandler.afterPropertiesSet();
        } else {
            auditingHandler = null;
        }

        AbstractAdvisorAutoProxyCreator abstractAdvisorAutoProxyCreator = new AbstractAdvisorAutoProxyCreator() {

            @Override
            protected List<Advisor> findCandidateAdvisors() {
                return auditingHandler == null
                        ? Collections.emptyList()
                        : Arrays.asList(
                                new AuditingAdvisor(auditingHandler, true),
                                new AuditingAdvisor(auditingHandler, false));
            }
        };
        abstractAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return abstractAdvisorAutoProxyCreator;
    }

}
