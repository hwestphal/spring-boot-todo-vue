package io.github.hwestphal.todo.config;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

@Component
class UserNameAuditor implements AuditorAware<String> {

    private final String userName;

    UserNameAuditor(@Value("${user.name}") String userName) {
        this.userName = userName;
    }

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(userName);
    }

}
