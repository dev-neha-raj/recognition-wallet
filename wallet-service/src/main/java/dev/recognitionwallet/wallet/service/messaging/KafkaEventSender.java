package dev.recognitionwallet.wallet.service.messaging;

import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Sends one event and waits for the broker's acknowledgement.
 * Returning only after the ack means "we told the caller it's accepted" is always true.
 * 
 */
@Component
public class KafkaEventSender{

private final KafkaTemplate<String, Object> kafkaTemplate;
private final Duration timeout;

public KafkaEventSender(KafkaTemplate<String, Object> kafkaTemplate,
@Value("${wallet.kafka.send-timeout:10s}") Duration timeout){
this.kafkaTemplate = kafkaTemplate;
this.timeout = timeout;
}
public void send(String topic, String key, Object event){
    try{
        kafkaTemplate.send(topic, key, event).get(timeout.toMillis(), TimeUnit.MILLISECONDS);
    } catch (InterruptedException e){
        Thread.currentThread().interrupt();
        throw new EventPublishException(topic, e);
    } catch(ExecutionException | TimeoutException | kafkaException e){
        throw new EventPublishException(topic, e);
    }
}
}
