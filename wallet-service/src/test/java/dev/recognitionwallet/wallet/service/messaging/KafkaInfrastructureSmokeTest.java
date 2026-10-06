package dev.recognitionwallet.wallet.service.messaging;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import dev.recognitionwallet.wallet.service.TestcontainersConfiguration;
import dev.recognitionwallet.wallet.service.MessagingTestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@Import({
    TestcontainersConfiguration.class,
    MessagingTestcontainersConfiguration.class
})
@SpringBootTest
class KafkaInfrastructureSmokeTest {

    @Autowired
    KafkaConnectionDetails kafka;

    @Test
    void walletTopicsAndDeadLetterTopicsExistWithThreePartitions() throws Exception {
        try (AdminClient admin = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers()))) {

            Set<String> names = admin.listTopics().names().get(30, TimeUnit.SECONDS);

            for (String topic : Topics.MAIN_TOPICS) {
                assertThat(names).contains(topic, Topics.deadLetterOf(topic));
            }

            Map<String, TopicDescription> described = admin
                    .describeTopics(List.of(Topics.AI_USAGE_INGESTED))
                    .allTopicNames()
                    .get(30, TimeUnit.SECONDS);

            assertThat(described.get(Topics.AI_USAGE_INGESTED).partitions())
                    .hasSize(3);
        }
    }

    @Test
    void stringMessageRoundTripsThroughBroker() throws Exception {
        String topic = "wallet.smoke-test." + UUID.randomUUID();

        try (AdminClient admin = AdminClient.create(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers()))) {

            admin.createTopics(
                    List.of(new NewTopic(topic, 1, (short) 1)))
                    .all()
                    .get(30, TimeUnit.SECONDS);
        }

        try (KafkaProducer<String, String> producer =
                     new KafkaProducer<>(Map.of(
                             ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers(),
                             ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                             ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class))) {

            producer.send(
                    new ProducerRecord<>(topic, "key-1", "hello redpanda"))
                    .get(30, TimeUnit.SECONDS);
        }

        try (KafkaConsumer<String, String> consumer =
                     new KafkaConsumer<>(Map.of(
                             ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers(),
                             ConsumerConfig.GROUP_ID_CONFIG, "smoke-" + UUID.randomUUID(),
                             ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                             ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                             ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class))) {

            consumer.subscribe(List.of(topic));

            List<ConsumerRecord<String, String>> received = new ArrayList<>();
            long deadline = System.currentTimeMillis() + 30_000;

            while (received.isEmpty() && System.currentTimeMillis() < deadline) {
                consumer.poll(Duration.ofMillis(500))
                        .forEach(received::add);
            }

            assertThat(received)
                    .singleElement()
                    .satisfies(record -> {
                        assertThat(record.key()).isEqualTo("key-1");
                        assertThat(record.value()).isEqualTo("hello redpanda");
                    });
        }
    }

    private String bootstrapServers() {
        return String.join(",", kafka.getBootstrapServers());
    }
}
