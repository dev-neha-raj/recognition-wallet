package dev.recognitionwallet.wallet.events.v1;

import org.apache.avro.AvroRuntimeException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/// Each generated event survives serialization unchanged, including null optional fields.
class EventRoundTripTest {

    // Millisecond precision on purpose: timestamp-millis drops anything finer.
    private static final Instant OCCURRED = Instant.parse("2026-10-01T09:15:30.123Z");
    private static final Instant LATER = Instant.parse("2026-10-01T09:15:31.456Z");

    @Test
    void aiUsageIngestedRoundTripsWithAllFields() throws Exception {
        AiUsageIngested event = AiUsageIngested.newBuilder()
            .setSourceEventId("gh-pr-1234")
            .setEmployeeId("emp-1")
            .setSourceTool("github-copilot")
            .setEventKind("PR_MERGED")
            .setOccurredAt(OCCURRED)
            .setIngestedAt(LATER)
            .setPromptText("Fix the null-check on line 42 of AuthService.java")
            .setAcceptanceRatio(0.8)
            .setFollowUpCount(1)
            .setTimeToAcceptanceSeconds(150L)
            .build();
    assertThat(AiUsageIngested.fromByteBuffer(event.toByteBuffer())).isEqualTo(event);            
    }

    @Test
    void aiUsageIngestedRoundTripsWithOptionalFieldsMissing() throws Exception {
        AiUsageIngested event = AiUsageIngested.newBuilder()
        .setSourceEventId("gh-pr-1235")
        .setEmployeeId("emp-1")
        .setSourceTool("github-copilot")
        .setEventKind("PR_MERGED")
        .setOccurredAt(OCCURRED)
        .setIngestedAt(LATER)
        .setPromptText("short")
        .build();

    AiUsageIngested copy =
        AiUsageIngested.fromByteBuffer(event.toByteBuffer());

    assertThat(copy.getAcceptanceRatio()).isNull();
    assertThat(copy.getFollowUpCount()).isNull();
    assertThat(copy.getTimeToAcceptanceSeconds()).isNull();
}

@Test
void qualityScoredRoundTripsForScoredAndFailedVerdicts() throws Exception {

    QualityScored scored = QualityScored.newBuilder()
        .setSourceEventId("gh-pr-1234")
        .setEmployeeId("emp-1")
        .setOccurredAt(OCCURRED)
        .setVerdict("SCORED")
        .setPrecision(4)
        .setContext(4)
        .setClarity(3)
        .setEfficiency(4)
        .setFeedback("Add the expected null-case behavior.")
        .setJudgeModel("claude-haiku-4-5-20251001")
        .setDecidedAt(LATER)
        .build();

    QualityScored failed = QualityScored.newBuilder()
        .setSourceEventId("gh-pr-1235")
        .setEmployeeId("emp-1")
        .setOccurredAt(OCCURRED)
        .setVerdict("FILTER_FAILED")
        .setReason("FILTER_FAILED: PROMPT_TOO_SHORT")
        .setDecidedAt(LATER)
        .build();

    assertThat(
        QualityScored.fromByteBuffer(scored.toByteBuffer())
    ).isEqualTo(scored);

    QualityScored failedCopy =
        QualityScored.fromByteBuffer(failed.toByteBuffer());

    assertThat(failedCopy).isEqualTo(failed);
    assertThat(failedCopy.getPrecision()).isNull();
}

@Test
void rewardDrawnRoundTrips() throws Exception {
    RewardDrawn event = RewardDrawn.newBuilder()
        .setSourceEventId("gh-pr-1234")
        .setEmployeeId("emp-1")
        .setOccurredAt(OCCURRED)
        .setOutcomeType("CLIPPED_BY_ANNUAL_CAP")
        .setPointsAwarded(5)
        .setPointsWouldHaveBeen(45)
        .setQualityTier("GOOD")
        .setComposite(3.75)
        .setDrawnAt(LATER)
        .build();

    assertThat(
        RewardDrawn.fromByteBuffer(event.toByteBuffer())
    ).isEqualTo(event);
}

@Test
void builderRejectsMissingRequiredField() {
    assertThatThrownBy(() -> RewardDrawn.newBuilder()
        .setSourceEventId("gh-pr-1234")
        .setEmployeeId("emp-1")
        .build())
        .isInstanceOf(AvroRuntimeException.class);
}

@Test
void timestampsKeepOnlyMillisecondPrecision() throws Exception {

    Instant withNanos =
        Instant.parse("2026-10-01T09:15:30.123456789Z");

    RewardDrawn event = RewardDrawn.newBuilder()
        .setSourceEventId("gh-pr-1")
        .setEmployeeId("emp-1")
        .setOccurredAt(withNanos)
        .setOutcomeType("NO_REWARD")
        .setPointsAwarded(0)
        .setReason("FILTER_FAILED: PROMPT_TOO_SHORT")
        .setDrawnAt(withNanos)
        .build();

    RewardDrawn copy =
        RewardDrawn.fromByteBuffer(event.toByteBuffer());

    assertThat(copy.getOccurredAt())
        .isEqualTo(Instant.parse("2026-10-01T09:15:30.123Z"));
}
}
