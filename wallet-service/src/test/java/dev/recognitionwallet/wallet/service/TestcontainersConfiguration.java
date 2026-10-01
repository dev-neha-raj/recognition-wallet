package dev.recognitionwallet.wallet.service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

/*
 * One PostgreSQLContainer is created for each test class, and the container is started before the tests are run. 
 * The container is stopped after the tests are completed.
 * This allows for a clean and isolated database environment for each test class.
 *
 * Declared as a bean so it lives in Spring's cached test context and is reused across test classes. 
 * This is important because starting and stopping containers can be slow, 
 * and we want to avoid doing it for every test method.
 */

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>("postgres:16");
    }
}