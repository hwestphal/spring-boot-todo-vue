package io.github.hwestphal.chrome;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import org.junit.rules.ExternalResource;

public class ChromeSessionRule extends ExternalResource implements Supplier<Session> {

    private final Supplier<SessionFactory> sessionFactorySupplier;
    private final Consumer<Session> sessionConfigurer;
    private Session session;

    public ChromeSessionRule(Supplier<SessionFactory> sessionFactorySupplier) {
        this(sessionFactorySupplier, null);
    }

    public ChromeSessionRule(Supplier<SessionFactory> sessionFactorySupplier, Consumer<Session> sessionConfigurer) {
        this.sessionFactorySupplier = Objects.requireNonNull(sessionFactorySupplier);
        this.sessionConfigurer = sessionConfigurer;
    }

    @Override
    public Session get() {
        return session;
    }

    @Override
    protected void before() throws Throwable {
        session = sessionFactorySupplier.get().create();
        if (sessionConfigurer != null) {
            sessionConfigurer.accept(session);
        }
    }

    @Override
    protected void after() {
        session.close();
        session = null;
    }

}
