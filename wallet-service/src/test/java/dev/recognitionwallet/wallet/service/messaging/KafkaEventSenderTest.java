package dev.recognitionwallet.wallet.service.messaging;

import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class KafkaEventSenderTest {

    @SuppressWarnings("unchecked")
    private final KafkaTemplate<String, Object> template = mock(KafkaTemplate.class);
    private final KafkaEventSender sender = new KafkaEventSender(template, Duration.ofMillis(200));

    @Test
    void returnsWhenBrokerAcknowledges() {
        when(template.send("t", "k", "v"))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        assertThatCode(() -> sender.send("t", "k", "v")).doesNotThrowAnyException();
        verify(template).send("t", "k", "v");
    }

    @Test
    void failedSendBecomesEventPublishException() {
        when(template.send("t", "k", "v"))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("broker down")));

        assertThatThrownBy(() -> sender.send("t", "k", "v"))
                .isInstanceOf(EventPublishException.class)
                .hasMessageContaining("t");
    }

    @Test
    void noAcknowledgementInTimeBecomesEventPublishException() {
        when(template.send("t", "k", "v")).thenReturn(new CompletableFuture<>());

        assertThatThrownBy(() -> sender.send("t", "k", "v"))
                .isInstanceOf(EventPublishException.class);
    }
}