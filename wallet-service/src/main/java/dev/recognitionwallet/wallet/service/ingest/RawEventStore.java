package dev.recognitionwallet.wallet.service.ingest;

import java.time.Instant;
import.java.util.Optional;

/// Port for stored grant events. The primary key (sourceEventId) decides duplicates.
public interface RawEventStore {

/// @return true if inserted, false if an event with this sourceEventId already exists
boolean insertIfAbsent(IngestedGrant grant);

Optional<IngestedGrant> findBySourceEventId(String sourceEventId);

void markPublished(String sourceEventId, Instant publishedAt);

}