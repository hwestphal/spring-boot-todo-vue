package io.github.hwestphal.todo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

import io.github.hwestphal.chrome.ChromeLauncherRule;
import io.github.hwestphal.chrome.ChromeSessionRule;

import io.webfolder.cdp.session.Session;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class TodoListChromeIntegrationTest {

    @ClassRule
    public static ChromeLauncherRule launcherRule = new ChromeLauncherRule("--headless", "--disable-gpu", "--lang=en-US");

    @Rule
    public ChromeSessionRule sessionRule = new ChromeSessionRule(launcherRule, session -> {
        session.enableNetworkLog();
        session.enableConsoleLog();
    });

    @Value("http://localhost:${local.server.port}${server.context-path:/}")
    private String baseUrl;

    @Test
    public void shouldCreateAScreenshot() throws IOException {
        Session session = sessionRule.get();
        session.navigate(baseUrl);
        session.waitDocumentReady();
        session.activate();
        try (OutputStream os = new FileOutputStream(Paths.get("target", "screenshot.png").toFile())) {
            os.write(session.captureScreenshot());
        }
    }

}
