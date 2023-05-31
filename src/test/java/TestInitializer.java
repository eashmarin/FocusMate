import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Map;

public class TestInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final PostgreSQLContainer<?> pgContainer = new PostgreSQLContainer<>("postgres:latest")
            .withExposedPorts(5432)
            .withInitScript("sql/schema.sql")
            .withPassword("qwerty")
            .withUsername("postgres");
    static {
        pgContainer.start();
    }

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
        overrideProperties(applicationContext, Map.ofEntries(
                Map.entry("spring.datasource.url", pgContainer.getJdbcUrl()),
                Map.entry("spring.datasource.username", pgContainer.getUsername()),
                Map.entry("spring.datasource.password", pgContainer.getPassword())
        ));
    }

    private void overrideProperties(ConfigurableApplicationContext applicationContext, Map<String, String> properties) {
        TestPropertyValues.of(properties).applyTo(applicationContext);
    }
}