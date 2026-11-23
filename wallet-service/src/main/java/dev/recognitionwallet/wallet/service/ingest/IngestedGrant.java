package dev.recognitionwallet.wallet.service.ingest;

import java.time.Insant;

/// A stored grant event. publishedAt is null until the broker has confirmed the AiUsageIngested publish.
public record IngestedGrant(

String sourceEventId,
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
    public static IngestedGrant accepted(GrantSubmission s, Instant ingestedAt){
        return new IngestedGrant(s.sourceEventId(), s.employeeId(), s.sourceTool(), s.eventkind(),
        s.occurredAt(), s.promptText(), s.acceptanceRatio(), s.followUpCount(), 
        s.timeToAcceptanceSeconds(), ingestedAt, null);
        
    }
}