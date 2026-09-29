package dev.recognitionwallet.wallet.common.reward;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QualityTierTest {

@Test
void excellentAtOrAbove4(){
assertThat(QualityTier.fromComposite(4.0)).isInstanceOf(QualityTier.Excellent.class);
assertThat(QualityTier.fromComposite(5.0)).isInstanceOf(QualityTier.Excellent.class);
}

@Test
void goodOrAbove3Under4(){
assertThat(QualityTier.fromComposite(3.0)).isInstanceOf(QualityTier.Good.class);
assertThat(QualityTier.fromComposite(3.99)).isInstanceOf(QualityTier.Good.class);
}

@Test
void passableOrAbove2Under3(){
assertThat(QualityTier.fromComposite(2.0)).isInstanceOf(QualityTier.Passable.class);
assertThat(QualityTier.fromComposite(2.99)).isInstanceOf(QualityTier.Passable.class);
}

@Test
void weakBelow2(){
assertThat(QualityTier.fromComposite(1.99)).isInstanceOf(QualityTier.Weak.class);
assertThat(QualityTier.fromComposite(0.0)).isInstanceOf(QualityTier.Weak.class);
}

@Test
void rejectsOutOfRange(){
    var ex1 = assertThrows(IllegalArgumentException.class, () -> QualityTier.fromComposite(-0.01));
    assertThat(ex1.getMessage()).contains("Composite score must be in the range [0.0, 5.0]");
    
    var ex2 = assertThrows(IllegalArgumentException.class, () -> QualityTier.fromComposite(5.01));
    assertThat(ex2.getMessage()).contains("Composite score must be in the range [0.0, 5.0]");

}

@Test
void exhaustiveSwitchCompilesWithoutDefault() {
    QualityTier tier = QualityTier.fromComposite(3.75);
    switch (tier) {
        case QualityTier.Excellent e -> {
            assertThat(e).isInstanceOf(QualityTier.Excellent.class);
        }
        case QualityTier.Good g -> {
            assertThat(g).isInstanceOf(QualityTier.Good.class);
        }
        case QualityTier.Passable p -> {
            assertThat(p).isInstanceOf(QualityTier.Passable.class);
        }
        case QualityTier.Weak w -> {
            assertThat(w).isInstanceOf(QualityTier.Weak.class);
        }
    }

}

private String label(QualityTier tier) {
    return switch (tier) {
        case QualityTier.Excellent e -> "Excellent";
        case QualityTier.Good g -> "Good";
        case QualityTier.Passable p -> "Passable";
        case QualityTier.Weak w -> "Weak";
    };
}
}
