package com.cuidar.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/**
 * Full integration test - requires Docker to spin up a real Postgres container.
 *
 * <p>The container is registered via {@link TestcontainersConfiguration} using
 * {@code @ServiceConnection}, so Spring auto-wires its JDBC URL into the
 * datasource.</p>
 *
 * <p>Container reuse is enabled ({@code withReuse(true)}) for faster subsequent runs.</p>
 */
@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ApiContainerIntegrationTest {

    @Test
    void contextLoadsWithRealPostgres() {
        // assertion is that the app boots with a real Postgres backing the datasource
    }
}