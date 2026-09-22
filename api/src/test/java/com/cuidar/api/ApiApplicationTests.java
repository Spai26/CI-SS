package com.cuidar.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Fast smoke test — boots the full Spring context against the H2 in-memory DB
 * (no Testcontainers, no Docker required) and verifies it loads cleanly.
 *
 * <p>Validates: profile wiring, JPA/JOOQ autoconfig, security, error handling,
 * and the domain-package scan. For tests that hit the real Postgres via
 * Testcontainers, see the {@code *ServiceIntegrationTest} classes in each
 * module package — those require Docker and are skipped on hosts where
 * Testcontainers can't reach the Docker daemon.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class ApiApplicationTests {

    @Test
    void contextLoads() {
        // intentionally empty - the assertion is that Spring can build the context
    }
}