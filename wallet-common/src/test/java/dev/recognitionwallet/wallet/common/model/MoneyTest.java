package dev.recognitionwallet.wallet.common.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Currency;
public class MoneyTest {

private static final Money INR = new Money(BigDecimal.valueOf(100), Money.INR);

@Test
void rejectNegativeAmount() {
    var ex = assertThrows(IllegalArgumentException.class, () -> new Money(BigDecimal.valueOf(-1), Money.INR));
    assertThat(ex.getMessage()).contains("Money cannot be negative");
}

@Test
void rejectsNullAmount() {
    var ex = assertThrows(NullPointerException.class, () -> new Money(null, Money.INR));
    assertThat(ex.getMessage()).contains("amount required");
}

@Test
void rejectsNullCurrency() {
    var ex = assertThrows(NullPointerException.class, () -> new Money(BigDecimal.valueOf(100), null));
    assertThat(ex.getMessage()).contains("currency required");
}

@Test
void enforceScale2WithHalfEvenRounding() {
    Money m = new Money(BigDecimal.valueOf(100.1234), Money.INR);
    assertThat(m.amount()).isEqualByComparingTo(new BigDecimal("100.12"));
    m = new Money(BigDecimal.valueOf(100.125), Money.INR);
    assertThat(m.amount()).isEqualByComparingTo(new BigDecimal("100.12"));
    m = new Money(BigDecimal.valueOf(100.126), Money.INR);
    assertThat(m.amount()).isEqualByComparingTo(new BigDecimal("100.13"));
}

@Test
void rupeesFactoryUsesInr() {
    assertThat(Money.rupees(100).currency()).isEqualTo(Money.INR);
    assertThat(Money.rupees(100).amount()).isEqualByComparingTo(new BigDecimal("100.00"));
}

@Test
void addSumsAmounts() {
    Money m1 = new Money(BigDecimal.valueOf(100), Money.INR);
    Money m2 = new Money(BigDecimal.valueOf(50), Money.INR);
    Money sum = m1.add(m2);
    assertThat(sum.amount()).isEqualByComparingTo(new BigDecimal("150.00"));
    assertThat(sum.currency()).isEqualTo(Money.INR);
}

@Test
void addRejectsDifferentCurrencies() {
    Money m1 = new Money(BigDecimal.valueOf(100), Money.INR);
    Money m2 = new Money(BigDecimal.valueOf(50), Currency.getInstance("USD"));
    var ex = assertThrows(IllegalArgumentException.class, () -> m1.add(m2));
    assertThat(ex.getMessage()).contains("Currency mismatch");
}

@Test
void isAtLeastCompareCorrectly() {
    Money m1 = new Money(BigDecimal.valueOf(100), Money.INR);
    Money m2 = new Money(BigDecimal.valueOf(50), Money.INR);
    Money m3 = new Money(BigDecimal.valueOf(100), Money.INR);
    assertThat(m1.isAtLeast(m2)).isTrue();
    assertThat(m1.isAtLeast(m3)).isTrue();
    assertThat(m2.isAtLeast(m1)).isFalse();
}
}