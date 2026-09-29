package dev.recognitionwallet.wallet.common.redemption;

import dev.recognitionwallet.wallet.common.model.Money;
import dev.recognitionwallet.wallet.common.model.Points;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Objects;

/*
sealed hierarchy representing the conversion policy for converting points to money in the wallet system.
The policies include:
- FixedRate: A fixed conversion rate from points to money, effective from a specific date and versioned.
Each policy is represented as a record implementing the ConversionPolicy interface.
v1 ships only with a fixed rate of 1 point = 1 rupee, effective from 2026-01-01.

*/ 
public sealed interface ConversionPolicy permits ConversionPolicy.FixedRate {

Money convert(Points points);

Instant effectiveFrom();

String version();

    record FixedRate(BigDecimal rupeesPoint, Instant effectiveFrom, String version) implements ConversionPolicy {
        public FixedRate {
            Objects.requireNonNull(rupeesPoint, "rupeesPoint required");
            Objects.requireNonNull(effectiveFrom, "effectiveFrom required");
            Objects.requireNonNull(version, "version required");
            if(version.isBlank()){
                throw new IllegalArgumentException("version cannot be blank");
            }
            if(rupeesPoint.signum() <= 0){
                throw new IllegalArgumentException("rupeesPoint must be positive: " + rupeesPoint);
            }
        }

        @Override
        public Money convert(Points points) {
           Objects.requireNonNull(points, "points required");
            BigDecimal cash = rupeesPoint
            .multiply(BigDecimal.valueOf(points.value()))
            .setScale(2, RoundingMode.HALF_EVEN);
            return new Money(cash, Money.INR);
        }

// v1.0 policy: 1 point = 1 rupee, effective from 2026-01-01
        public static FixedRate v1(){
            return new FixedRate
            (BigDecimal.ONE, Instant.parse("2026-01-01T00:00:00Z"), "v1.0");
        }
    }
}
