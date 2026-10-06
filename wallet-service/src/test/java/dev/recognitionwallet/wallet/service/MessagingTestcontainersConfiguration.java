package dev.recognitionwallet.wallet.service;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.redpanda.RedpandaContainer;
import org.springframework.context.annotation.Bean;

//Messaging infrastructure for full-context tests. Import together with TestcontainersConfiguration to boot the full application context with a Redpanda broker.

@TestConfiguration(proxyBeanMethods = false)
public class MessagingTestcontainersConfiguration {
   
   static final String REDPANDA_IMAGE = "docker.redpanda.com/redpandadata/redpanda:v24.2.7";

@Bean
@ServiceConnection
RedpandaContainer redpandaContainer() {
    return new RedpandaContainer(REDPANDA_IMAGE);
}
}
