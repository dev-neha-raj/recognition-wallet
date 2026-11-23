

/// Publishes AiusageIngested, Keyed by employeeId so one employee's events stay in order.

@Component
public class KafkaGrantEventPublisher implements GrantEventPublisher {

private final KafkaEventSender sender;

    public KafkaGrantEventPublisher(KafkaEventSender sender) {
        this.sender = sender;
    }

    @Override
    public void publishIngested(IngestedGrant grant) {
        sender.send(Topics.AI_USAGE_INGESTED, grant.employeeId(), toEvent(grant));
    }

    static AiUsageIngested toEvent(IngestedGrant grant) {
        return AiUsageIngested.newBuilder()
                .setSourceEventId(grant.sourceEventId())
                .setEmployeeId(grant.employeeId())
                .setSourceTool(grant.sourceTool())
                .setEventKind(grant.eventKind())
                .setOccurredAt(grant.occurredAt())
                .setIngestedAt(grant.ingestedAt())
                .setPromptText(grant.promptText())
                .setAcceptanceRatio(grant.acceptanceRatio())
                .setFollowUpCount(grant.followUpCount())
                .setTimeToAcceptanceSeconds(grant.timeToAcceptanceSeconds())
                .build();
    }
}