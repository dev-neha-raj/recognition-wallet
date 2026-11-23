package dev.recognitionwallet.wallet.service.ingest;

///port for publishing an accepted grant. Throws EventPublishException if the broker doesn't confirm.
public interface GrantEventPublisher {

    void publishIngested(IngestedGrant grant);
    
}