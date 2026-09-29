package dev.recognitionwallet.wallet.common.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
public class WalletTest {
 
private static final Instant T0 = Instant.parse("2026-01-01T00:00:00Z");

@Test
void enrollProductACtiveWallet() {
    Wallet wallet = Wallet.enrollProduct("emp-1", T0);
    assertThat(wallet.employeeId()).isEqualTo("emp-1");
    assertThat(wallet.isActive()).isTrue();
    assertThat(wallet.status()).isInstanceOf(WalletStatus.Active.class);
    assertThat(wallet.createdAt()).isEqualTo(T0);
}

@Test
void enrollRejectsBlankEmployeeId() {
    var ex = assertThrows(IllegalArgumentException.class, () -> Wallet.enrollProduct("", T0));
    assertThat(ex.getMessage()).contains("employeeId cannot be blank");
}

@Test
void closeTransitionsWalletToClosed() {
    Wallet wallet = Wallet.enrollProduct("emp-1", T0);
    Instant closedAt = Instant.parse("2026-01-02T00:00:00Z");
    Wallet closedWallet = wallet.close(closedAt, "Employee left the company");
    assertThat(closedWallet.isActive()).isFalse();
    assertThat(closedWallet.status()).isInstanceOf(WalletStatus.Closed.class);
    WalletStatus.Closed closedStatus = (WalletStatus.Closed) closedWallet.status();
    assertThat(closedStatus.closedAt()).isEqualTo(closedAt);
    assertThat(closedStatus.reason()).isEqualTo("Employee left the company");
}

@Test
void closeReturnsNewInstanceOriginalUnchanged() {
    Wallet wallet = Wallet.enrollProduct("emp-1", T0);
    Instant closedAt = Instant.parse("2026-01-02T00:00:00Z");
    Wallet closedWallet = wallet.close(closedAt, "Employee left the company");
    assertThat(wallet.isActive()).isTrue();
    assertThat(closedWallet.isActive()).isFalse();
    assertThat(closedWallet.id()).isEqualTo(wallet.id());    
}

@Test
void cannotCloseAlreadyClosedWallet() {
    Wallet wallet = Wallet.enrollProduct("emp-1", T0);
    Instant closedAt = Instant.parse("2026-01-02T00:00:00Z");
    Wallet closedWallet = wallet.close(closedAt, "Employee left the company");
    var ex = assertThrows(IllegalStateException.class, () -> closedWallet.close(closedAt.plusSeconds(3600), "Attempt to close again"));
    assertThat(ex.getMessage()).contains("Wallet is already closed");
}
}