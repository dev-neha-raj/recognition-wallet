package dev.recognitionwallet.wallet.common.reward;
/* 
QualityTier represents the quality of a reward based on a composite score.
The composite score is a double value in the range [0.0, 5.0], where higher values indicate better quality.
The QualityTier interface is a sealed interface, allowing only specific implementations:
- Excellent: Represents a composite score of 4.0 or higher.
- Good: Represents a composite score of 3.0 to 3.99.
- Passable: Represents a composite score of 2.0 to 2.99.
- Weak: Represents a composite score below 2.0.
The fromComposite method is a static factory method that takes a composite score and returns the corresponding QualityTier implementation. It throws an IllegalArgumentException if the composite score is outside the valid range.

*/

public sealed interface QualityTier
    permits QualityTier.Excellent, 
    QualityTier.Good, 
    QualityTier.Passable,
    QualityTier.Weak {

record Excellent() implements QualityTier {}
record Good() implements QualityTier {}
record Passable() implements QualityTier {}
record Weak() implements QualityTier {}

static QualityTier fromComposite(double composite) {
    if(composite < 0.0 || composite > 5.0) {
        throw new IllegalArgumentException
        ("Composite score must be in the range [0.0, 5.0]: " + composite);
    }  
    if (composite >= 4.0) {
        return new Excellent();
    }
    if (composite >= 3.0) {
        return new Good();
    }
    if (composite >= 2.0) {
        return new Passable();
    }
        return new Weak();
    }
}