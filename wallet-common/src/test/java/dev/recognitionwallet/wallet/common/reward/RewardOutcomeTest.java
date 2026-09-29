package dev.recognitionwallet.wallet.common.reward;

import dev.recognitionwallet.wallet.common.model.Points;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RewardOutcomeTest {

@Test
void noRewardCarriesZeroPoints(){
    RewardOutcome outcome = RewardOutcome.noReward();
    assertThat(outcome.awardedPoints().value()).isEqualTo(0);
}

@Test
void microEnforcesRange(){
    assertThat(RewardOutcome.microReward(2).awardedPoints().value()).isEqualTo(2);
    assertThat(RewardOutcome.microReward(19).awardedPoints().value()).isEqualTo(19);

    var ex = assertThrows(IllegalArgumentException.class, () -> RewardOutcome.microReward(-1));
    assertThat(ex.getMessage()).contains("Micro range is");
}

@Test
void smallEnforcesRange(){
    assertThat(RewardOutcome.smallReward(20).awardedPoints().value()).isEqualTo(20);
    assertThat(RewardOutcome.smallReward(99).awardedPoints().value()).isEqualTo(99);

    var ex = assertThrows(IllegalArgumentException.class, () -> RewardOutcome.smallReward(-1));
    assertThat(ex.getMessage()).contains("Small range is");
}

@Test
void mediumEnforcesRange(){
    assertThat(RewardOutcome.mediumReward(100).awardedPoints().value()).isEqualTo(100);
    assertThat(RewardOutcome.mediumReward(499).awardedPoints().value()).isEqualTo(499);

    var ex = assertThrows(IllegalArgumentException.class, () -> RewardOutcome.mediumReward(-1));
    assertThat(ex.getMessage()).contains("Medium range is");
}

@Test
void largeEnforcesRange(){
    assertThat(RewardOutcome.largeReward(500).awardedPoints().value()).isEqualTo(500);
    assertThat(RewardOutcome.largeReward(1999).awardedPoints().value()).isEqualTo(1999);

    var ex = assertThrows(IllegalArgumentException.class, () -> RewardOutcome.largeReward(-1));
    assertThat(ex.getMessage()).contains("Large range is");
}

@Test
void jackpotEnforcesRange(){
    assertThat(RewardOutcome.jackpotReward(2000).awardedPoints().value()).isEqualTo(2000);
    assertThat(RewardOutcome.jackpotReward(10000).awardedPoints().value()).isEqualTo(10000);

    var ex = assertThrows(IllegalArgumentException.class, () -> RewardOutcome.jackpotReward(-1));
    assertThat(ex.getMessage()).contains("Jackpot range is");
}

@Test
void clippedByCapRequiredDownwardClip(){
    RewardOutcome outcome = RewardOutcome.clippedByAnnualCap(
        Points.of(150),
        Points.of(2000));
    assertThat(outcome.awardedPoints().value()).isEqualTo(150);

    var ex = assertThrows(IllegalArgumentException.class, () -> RewardOutcome.clippedByAnnualCap(
        Points.of(200),
        Points.of(100)));
    assertThat(ex.getMessage()).contains("awarded");
}

@Test
void exhaustiveSwitchCompilesWithoutDefault() {
    RewardOutcome outcome = RewardOutcome.noReward();
    switch (outcome) {
        case RewardOutcome.NoReward nr -> {
            assertThat(nr.awardedPoints().value()).isEqualTo(0);
        }
        case RewardOutcome.Micro mr -> {
            assertThat(mr.awardedPoints().value()).isBetween(2, 19);
        }
        case RewardOutcome.Small sr -> {
            assertThat(sr.awardedPoints().value()).isBetween(20, 99);
        }
        case RewardOutcome.Medium mr -> {
            assertThat(mr.awardedPoints().value()).isBetween(100, 499);
        }
        case RewardOutcome.Large lr -> {
            assertThat(lr.awardedPoints().value()).isBetween(500, 1999);
        }
        case RewardOutcome.Jackpot jr -> {
            assertThat(jr.awardedPoints().value()).isBetween(2000, 10000);
        }
        case RewardOutcome.ClippedByAnnualCap cc -> {
            assertThat(cc.awardedPoints().value()).isLessThanOrEqualTo(cc.wouldHaveBeen().value());
        }
    }
}

private String label(RewardOutcome outcome) {
    return switch (outcome) {
        case RewardOutcome.NoReward nr -> "No Reward";
        case RewardOutcome.Micro mr -> "Micro Reward: " + mr.awardedPoints().value();
        case RewardOutcome.Small sr -> "Small Reward: " + sr.awardedPoints().value();
        case RewardOutcome.Medium mr -> "Medium Reward: " + mr.awardedPoints().value();
        case RewardOutcome.Large lr -> "Large Reward: " + lr.awardedPoints().value();
        case RewardOutcome.Jackpot jr -> "Jackpot Reward: " + jr.awardedPoints().value();
        case RewardOutcome.ClippedByAnnualCap cc -> "Clipped by Annual Cap: " + cc.awardedPoints().value() + "/" + cc.wouldHaveBeen().value();
    };
}
}