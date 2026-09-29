package dev.recognitionwallet.wallet.common.model;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PointsTest {

@Test
public void rejectNegativePoints() {
    var ex = assertThrows(IllegalArgumentException.class, () -> new Points(-1));
    assertThat(ex.getMessage()).contains("Points value cannot be negative");
}

@Test
public void zeroIsAllowed() {
   assertThat(new Points(0).value()).isEqualTo(0);
   assertThat(Points.of(42).value()).isEqualTo(42);
}

@Test
public void addSumsValues() {
    assertThat(new Points(30).add(new Points(5)).value()).isEqualTo(35);
}

@Test
public void subtractSubtractsValues() {
    assertThat(new Points(30).subtract(new Points(5)).value()).isEqualTo(25);
}

@Test
public void subtractNegativeThrows() {
    var ex = assertThrows(IllegalArgumentException.class, () -> new Points(5).subtract(new Points(10)));
    assertThat(ex.getMessage()).contains("Resulting points cannot be negative");
}

@Test
public void addRejectOverflow() {
    var ex = assertThrows(IllegalArgumentException.class, () -> new Points(Integer.MAX_VALUE).add(new Points(1)));
    assertThat(ex.getMessage()).contains("Resulting points cannot exceed Integer.MAX_VALUE");
}

@Test
public void isAtLeastCompareCorrectly() {
    assertThat(new Points(10).isAtLeast(new Points(5))).isTrue();
    assertThat(new Points(5).isAtLeast(new Points(5))).isTrue();
    assertThat(new Points(4).isAtLeast(new Points(5))).isFalse();
}
}