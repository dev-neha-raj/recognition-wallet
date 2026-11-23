package dev.recognitionwallet.wallet.service.messaging;

/// The broker did not confirm a publish in time. Callers should treat the event as not sent;
public class EventPublishException extends RuntimeException {

public EventPublishException(String topic, Throwable cause) {
    super("Failed to publish to" + topic, cause);
}
}