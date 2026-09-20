package com.cuidar.api;

import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.cuidar.api.jooq.generated.Tables.USUARIO;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Fast smoke test - boots Spring against the local Postgres (docker-compose)
 * and verifies jOOQ can query it.
 *
 * <p>Validates: profile wiring, datasource autoconfig, Flyway, jOOQ DSLContext,
 * generated classes, security, error handling, and the domain-package scan.</p>
 *
 * <p>For the full integration test (with a Testcontainers Postgres) see
 * {@link ApiContainerIntegrationTest}.</p>
 */
@SpringBootTest
@ActiveProfiles("dev")
class ApiApplicationTests {

    @Autowired
    private DSLContext dsl;

    @Test
    void contextLoads() {
        // assert jOOQ can talk to the DB through the generated metadata
        Integer count = dsl.selectCount()
                .from(USUARIO)
                .fetchOne(0, Integer.class);
        assertThat(count).isNotNull().isGreaterThanOrEqualTo(0);
    }
}