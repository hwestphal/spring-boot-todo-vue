package io.github.hwestphal.auditing;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.auditing.AuditingHandler;

@SuppressWarnings("serial")
class AuditingAdvisor extends AbstractPointcutAdvisor {

    private final AuditingHandler auditingHandler;
    private final boolean create;

    AuditingAdvisor(AuditingHandler auditingHandler, boolean create) {
        this.auditingHandler = auditingHandler;
        this.create = create;
    }

    @Override
    public Pointcut getPointcut() {
        return new StaticMethodMatcherPointcut() {

            @Override
            public boolean matches(Method method, Class<?> targetClass) {
                return create && AnnotationUtils.findAnnotation(method, Create.class) != null
                        || !create && AnnotationUtils.findAnnotation(method, Modify.class) != null;
            }
        };
    }

    @Override
    public MethodInterceptor getAdvice() {
        return invocation -> {
            for (Object arg : invocation.getArguments()) {
                if (create) {
                    auditingHandler.markCreated(arg);
                } else {
                    auditingHandler.markModified(arg);
                }
            }
            return invocation.proceed();
        };
    }

}
