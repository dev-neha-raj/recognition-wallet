package dev.recognitionwallet.wallet.service.ingest;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/// One row of raw_events
@Table("raw_events")
public record RawEventRow(
@Id String sourceEventId,
String employeeId,
String sourceTool,
String eventKind,
Instant occurredAt,
String promptText,
Double acceptanceRatio,
Integer followUpCount,
Long timeToAcceptanceSeconds,
Instant ingestedAt,
Instant publishedAt

){

static RawEventRow from(IngestedGrant g){
    return new RawEventRow(g.sourceEventId(), g.employeeId(), g.sourceTool(), g.eventKind(),
        g.occurredAt(), g.promptText(), g.acceptanceRatio(), g.followUpCount(),
        g.timeToAcceptanceSeconds(), g.ingestedAt(), g.publishedAt());

}
IngestedGrant toGrant(){
    return new IngestedGrant(sourceEventId, employeeId, sourceTool, eventKind, occurredAt, promptText,
            acceptanceRatio, followUpCount, timeToAcceptanceSeconds, ingestedAt, publishedAt);
}
}