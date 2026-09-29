package dev.recognitionwallet.wallet.common.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * External currency unit for cash payouts. 
 * 
 * uses BigDecimal with Half-Even rounding at scale 2 - the standard for money.
 * Distinct type from points; conversion happens only via ConversionPolicy (see the redemption package).
 * 
 */

public record Money(BigDecimal amount, Currency currency) {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;
    public static final Currency INR = Currency.getInstance("INR");
    
public Money {
    Objects.requireNonNull(amount, "amount required");
    Objects.requireNonNull(currency, "currency required");
        if (amount.signum() < 0) {
            throw new IllegalArgumentException("Money cannot be negative: " + amount);
        }
       amount = amount.setScale(SCALE, ROUNDING);
}

public static Money rupees(long value){
return new Money(BigDecimal.valueOf(value), INR);
}

public static Money zero(Currency currency){
return new Money(BigDecimal.ZERO, currency);
}

public Money add(Money other){
    requireSameCurrency(other);
    return new Money(this.amount.add(other.amount), currency);
}

public Money subtract(Money other){
    requireSameCurrency(other);
    return new Money(this.amount.subtract(other.amount), currency);
}

public boolean isAtLeast(Money other){
    requireSameCurrency(other);
    return this.amount.compareTo(other.amount) >= 0;
}

public boolean isZero(){
    return amount.signum() == 0;
}

private void requireSameCurrency(Money other){
    if(!this.currency.equals(other.currency)){
        throw new IllegalArgumentException("Currency mismatch: " + currency + " vs " + other.currency);
    }
}
}