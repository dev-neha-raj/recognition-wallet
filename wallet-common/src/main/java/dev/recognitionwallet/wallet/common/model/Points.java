package dev.recognitionwallet.wallet.common.model;
/**
 * Internal accounting unit for wallet rewards.
 * 
 * Points are a distinct type of Money - the compiler prevents accidental mixing of the two types.
 * Only ConversionPolicy can turn points into Money(see the redemption package).
 * 
 */
public record Points( int value) {
    public static final Points ZERO = new Points(0);
    public Points {
        if (value < 0) {
            throw new IllegalArgumentException("Points value cannot be negative" + value);
        }   
    }
public static Points of(int value) {
        return new Points(value);
    }

public Points add(Points other) {
        try {
            return new Points(Math.addExact(this.value, other.value));
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Resulting points cannot exceed Integer.MAX_VALUE");
        }
    }

public Points subtract(Points other) {
        if (other.value > this.value) {
            throw new IllegalArgumentException("Resulting points cannot be negative");
        }
        try {
            return new Points(Math.subtractExact(this.value, other.value));
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Resulting points cannot be negative");
        }
    }

public boolean isAtLeast(Points other) {
        return this.value >= other.value;
    }

public boolean isZero() {
        return this.value == 0;
    }
}
