package dev.recognitionwallet.wallet.service.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaTopicsConfigTest {

    @Test
    void declaresEveryMainTopicAndItsDeadLetterTwin() {
        List<NewTopic> topics = KafkaTopicsConfig.newTopics(new WalletTopicProperties(3, 1));

        assertThat(topics).extracting(NewTopic::name).containsExactlyInAnyOrder(
            "wallet.ai-usage-ingested.v1", "wallet.ai-usage-ingested.v1.dlt",
            "wallet.judge-requested.v1", "wallet.judge-requested.v1.dlt",
            "wallet.quality-scored.v1", "wallet.quality-scored.v1.dlt",
            "wallet.reward-drawn.v1", "wallet.reward-drawn.v1.dlt");
    }

    @Test
    void appliesConfiguredPartitionsAndReplicasToEveryTopic() {
        List<NewTopic> topics = KafkaTopicsConfig.newTopics(new WalletTopicProperties(6, 3));

        assertThat(topics).allSatisfy(topic -> {
            assertThat(topic.numPartitions()).isEqualTo(6);
            assertThat(topic.replicationFactor()).isEqualTo((short) 3);
        });
    }

    @Test
    void deadLetterNameAppendsSuffix() {
        assertThat(Topics.deadLetterOf("a.b.v1")).isEqualTo("a.b.v1.dlt");
    }
}