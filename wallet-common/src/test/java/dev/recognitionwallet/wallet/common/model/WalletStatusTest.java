package dev.recognitionwallet.wallet.common.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WalletStatusTest {

@Test
void activeSingletonHasStableIdentity() {
    WalletStatus.Active a1 = WalletStatus.Active.INSTANCE;
    WalletStatus.Active a2 = WalletStatus.Active.INSTANCE;
    assertThat(a1).isSameAs(a2);
}

@Test
void closedRequiresReason(){

    Instant closedAt = Instant.parse("2026-01-02T00:00:00Z");
    var ex1 = assertThrows(NullPointerException.class, () -> new WalletStatus.Closed(closedAt, null));
    assertThat(ex1.getMessage()).contains("reason required");

    var ex2 = assertThrows(IllegalArgumentException.class, () -> new WalletStatus.Closed(closedAt, ""));
    assertThat(ex2.getMessage()).contains("reason cannot be blank");

}

@Test
void exhaustiveSwitchCompilesWithoutDefault() {
    WalletStatus status = WalletStatus.Active.INSTANCE;
    switch (status) {
        case WalletStatus.Active a -> {
            assertThat(a).isSameAs(WalletStatus.Active.INSTANCE);
        }
        case WalletStatus.Closed c -> {
            assertThat(c.closedAt()).isNotNull();
            assertThat(c.reason()).isNotBlank();
        }
    }
}

private String describe(WalletStatus status) {
    return switch (status) {
        case WalletStatus.Active a -> "Active";
        case WalletStatus.Closed c -> "Closed: " + c.reason();
    };
}
}


