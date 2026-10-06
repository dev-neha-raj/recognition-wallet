package dev.recognitionwallet.wallet.service.messaging;

import java.util.List;
import java.util.stream.Stream;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * Declares every wallet topic and its dead-Letter twin as a NewTopic bean, so that Spring Boot will create them on startup.KafkaAdmin creates missing topics at startup.
 * 
 * A dead-letter topic must have as many partitions as its source topic, because a failed record is
 * parked in the same partition number it come from.
 * 
 */

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(WalletTopicProperties.class)
class KafkaTopicsConfig {
    
    @Bean
    KafkaAdmin.NewTopics walletTopics(WalletTopicProperties properties) {
        return new KafkaAdmin.NewTopics(
            newTopics(properties).toArray(NewTopic[]::new));

}

static List<NewTopic> newTopics(WalletTopicProperties properties) {
    return Topics.MAIN_TOPICS.stream()
            .flatMap(topic -> Stream.of(topic, Topics.deadLetterOf(topic)))
            .map(name -> TopicBuilder.name(name)
                    .partitions(properties.partitions())
                    .replicas(properties.replicas())
                    .build())
                    .toList();
}

}