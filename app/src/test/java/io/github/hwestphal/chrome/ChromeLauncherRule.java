package io.github.hwestphal.chrome;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import io.webfolder.cdp.AdaptiveProcessManager;
import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.SessionFactory;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class ChromeLauncherRule implements TestRule, Supplier<SessionFactory> {

    private final List<String> args;
    private SessionFactory sessionFactory;

    public ChromeLauncherRule(String... args) {
        this.args = Arrays.asList(args);
    }

    @Override
    public SessionFactory get() {
        return sessionFactory;
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {

            @Override
            public void evaluate() throws Throwable {
                Launcher launcher = new Launcher(freeLocalPort());
                if (launcher.findChrome() != null) {
                    launcher.setProcessManager(new AdaptiveProcessManager());
                    sessionFactory = launcher.launch(args);
                    try {
                        base.evaluate();
                    } finally {
                        sessionFactory.close();
                        sessionFactory = null;
                        launcher.kill();
                    }
                } else {
                    System.err.println("\nchrome not found, test suite will be ignored\n");
                }
            }
        };
    }

    private static int freeLocalPort() throws IOException {
        ServerSocket socket = new ServerSocket(0);
        try {
            return socket.getLocalPort();
        } finally {
            socket.close();
        }
    }

}
