package dev.recognitionwallet.wallet.service.ingest;

import org.springframework.stereotype.Service;

import java.time.Clock;

/***
 * Accepts a grant: eligibility check, store once, publish once.
 * 
 * A row whose publish never succeeded (publishedAt null) is published again when the caller retries,
 * which repairs the gap between the database write and the kafka write
 * 
 */

@Service
public class GrantIngestService {

    private final GrantPreflight preflight;
    private final RawEventStore rawEvents;
    private final GrantEventPublisher publisher;
    private final Clock clock;

    public GrantIngestService(GrantPreflight preflight, RawEventStore rawEvents, GrantEventPublisher publisher, Clock clock) {
        this.preflight = preflight;
        this.rawEvents = rawEvents;
        this.publisher = publisher;
        this.clock = clock;
    }

public IngestResult ingest(GrantSubmission submission){
    preflight.check(submission);

    IngestedGrant grant = IngestedGrant.accepted(submission, clock.instant());
    if (rawEvents.inserIfAbsent(grant)){
        publishAndMark(grant);
        return new IngestResult(grant.sourceEventId(), IngestStatus.ACCEPTED);
    }

    IngestedGrant existing = rawEvents.findBySourceEventId(grant.sourceEventId()).orElseThrow();
     if (existing.publishedAt() == null){
        publishAndMark(existing);
        return new IngestResult(existing.sourceEventId(), IngestStatus.ACCEPTED);
     }
    return new IngestResult(existing.sourceEventId(), IngestStatus.DUPLICATE);
}

private void publishAndMark(IngestedGrant grant){
    publisher.publishIngested(grant);
    rawEvents.markPublished(grant.sourceEventId(), clock.instant());
}

}