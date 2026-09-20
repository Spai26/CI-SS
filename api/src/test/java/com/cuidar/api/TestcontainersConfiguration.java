package com.cuidar.api;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Spins up a real PostgreSQL container for integration tests and registers its
 * JDBC connection properties with Spring Boot via {@link ServiceConnection}.
 *
 * <p>The image matches the one used in {@code docker-compose.yml} at the project
 * root, so tests exercise the same PGMQ-enabled Postgres that runs in dev.</p>
 */
@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    private static final DockerImageName POSTGRES_IMAGE =
            DockerImageName.parse("ghcr.io/pgmq/pg18-pgmq:v1.10.0")
                    .asCompatibleSubstituteFor("postgres");

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(POSTGRES_IMAGE)
                .withDatabaseName("cuidar_test")
                .withUsername("test")
                .withPassword("test")
                .withReuse(true);
    }
}