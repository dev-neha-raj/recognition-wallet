package dev.recognitionwallet.wallet.common.redemption;

import dev.recognitionwallet.wallet.common.model.Money;
import dev.recognitionwallet.wallet.common.model.Points;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ConversionPolicyTest {

@Test
void v1PolicyConvertsPointsToRewardOutcome() {
    ConversionPolicy policy = ConversionPolicy.FixedRate.v1();
    Money money = policy.convert(new Points(100));
    assertThat(money.amount()).isEqualByComparingTo(new BigDecimal("100.00"));
    assertThat(money.currency()).isEqualTo(Money.INR);
}

@Test
void v1PolicyMetadata(){
    ConversionPolicy policy = ConversionPolicy.FixedRate.v1();
    assertThat(policy.effectiveFrom()).isEqualTo(Instant.parse("2026-01-01T00:00:00Z"));
    assertThat(policy.version()).isEqualTo("v1.0");
    assertThat(((ConversionPolicy.FixedRate)policy).rupeesPoint()).isEqualTo(BigDecimal.ONE);
}

@Test 
void convertZeroPointsProducesZeroMoney() {
    ConversionPolicy policy = ConversionPolicy.FixedRate.v1();
    Money money = policy.convert(new Points(0));
    assertThat(money.amount()).isEqualByComparingTo(new BigDecimal("0.00"));
    assertThat(money.currency()).isEqualTo(Money.INR);
}

@Test
void rejectsNonPositiveRate(){
    var ex1 = assertThrows(IllegalArgumentException.class, () -> new ConversionPolicy.FixedRate(BigDecimal.ZERO, Instant.parse("2026-01-01T00:00:00Z"), "v1.0"));
    assertThat(ex1.getMessage()).contains("rupeesPoint must be positive");

    var ex2 = assertThrows(IllegalArgumentException.class, () -> new ConversionPolicy.FixedRate(BigDecimal.valueOf(-1), Instant.parse("2026-01-01T00:00:00Z"), "v1.0"));
    assertThat(ex2.getMessage()).contains("rupeesPoint must be positive");
}

@Test
void rejectsBlankVersion(){
    var ex = assertThrows(IllegalArgumentException.class, () -> new ConversionPolicy.FixedRate(BigDecimal.ONE, Instant.parse("2026-01-01T00:00:00Z"), ""));
    assertThat(ex.getMessage()).contains("version cannot be blank");
}

@Test
void exhaustiveSwitchCompilesWithoutDefault() {
    ConversionPolicy policy = ConversionPolicy.FixedRate.v1();
    switch (policy) {
        case ConversionPolicy.FixedRate f -> {
            assertThat(f.rupeesPoint()).isEqualTo(BigDecimal.ONE);
            assertThat(f.effectiveFrom()).isEqualTo(Instant.parse("2026-01-01T00:00:00Z"));
            assertThat(f.version()).isEqualTo("v1.0");
        }
    }
}

private String describe(ConversionPolicy policy) {
    return switch (policy) {
        case ConversionPolicy.FixedRate f -> "FixedRate: " + f.rupeesPoint() + " rupees/point, effective from " + f.effectiveFrom() + ", version " + f.version();
    };

}
}
