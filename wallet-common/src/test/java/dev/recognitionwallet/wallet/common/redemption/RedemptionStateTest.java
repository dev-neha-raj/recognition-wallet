package dev.recognitionwallet.wallet.common.redemption;

import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RedemptionStateTest {

private static final Instant T0 = Instant.parse("2026-01-01T00:00:00Z");

@Test
void requestedTracksTimestamp(){
    assertThat(RedemptionState.Requested.of(T0).requestedAt()).isEqualTo(T0);
}

@Test
void rejectedRequiresReason() {
    Instant rejectedAt = Instant.parse("2026-01-02T00:00:00Z");
    var ex1 = assertThrows(NullPointerException.class, () -> new RedemptionState.Rejected(rejectedAt, null));
    assertThat(ex1.getMessage()).contains("reason required");

    var ex2 = assertThrows(IllegalArgumentException.class, () -> new RedemptionState.Rejected(rejectedAt, ""));
    assertThat(ex2.getMessage()).contains("reason cannot be blank");
}

@Test
void settledRequriesBankReference(){
    Instant settledAt = Instant.parse("2026-01-02T00:00:00Z");
    var ex1 = assertThrows(NullPointerException.class, () -> new RedemptionState.Settled(settledAt, null));
    assertThat(ex1.getMessage()).contains("bank reference required");

    var ex2 = assertThrows(IllegalArgumentException.class, () -> new RedemptionState.Settled(settledAt, ""));
    assertThat(ex2.getMessage()).contains("bank reference cannot be blank");
}

@Test
void failedRequiresReason() {
    Instant failedAt = Instant.parse("2026-01-02T00:00:00Z");
    var ex1 = assertThrows(NullPointerException.class, () -> new RedemptionState.Failed(failedAt, null));
    assertThat(ex1.getMessage()).contains("reason required");

    var ex2 = assertThrows(IllegalArgumentException.class, () -> new RedemptionState.Failed(failedAt, ""));
    assertThat(ex2.getMessage()).contains("reason cannot be blank");
}

@Test 
void exhaustiveSwitchCompilesWithoutDefault() {
    RedemptionState state = RedemptionState.Requested.of(T0);
    switch (state) {
        case RedemptionState.Requested r -> {
            assertThat(r.requestedAt()).isEqualTo(T0);
        }
        case RedemptionState.Approved a -> {
            assertThat(a.approvedAt()).isNotNull();
        }
        case RedemptionState.Rejected r -> {
            assertThat(r.rejectedAt()).isNotNull();
            assertThat(r.reason()).isNotBlank();
        }
        case RedemptionState.Settled s -> {
            assertThat(s.settledAt()).isNotNull();
            assertThat(s.bankReference()).isNotBlank();
        }
        case RedemptionState.Failed f -> {
            assertThat(f.failedAt()).isNotNull();
            assertThat(f.reason()).isNotBlank();
        }
        case RedemptionState.Reversed r -> {
            assertThat(r.reversedAt()).isNotNull();
            assertThat(r.reason()).isNotBlank();
        }
    }
}

private String describe(RedemptionState state) {
    return switch (state) {
        case RedemptionState.Requested r -> "Requested";
        case RedemptionState.Approved a -> "Approved";
        case RedemptionState.Rejected r -> "Rejected: " + r.reason();
        case RedemptionState.Settled s -> "Settled: " + s.bankReference();
        case RedemptionState.Failed f -> "Failed: " + f.reason();
        case RedemptionState.Reversed r -> "Reversed: " + r.reason();
    };
}
}