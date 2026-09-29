package dev.recognitionwallet.wallet.common.reward;

import dev.recognitionwallet.wallet.common.model.Points;
import java.util.Objects;

/*
    * Represents the outcome of a reward calculation.
    * 
    * The outcome can be one of several types, each representing a different level of reward:
    * - NoReward: No points are awarded, with a reason provided.
    * - Micro: A small number of points are awarded (2-19).
    * - Small: A moderate number of points are awarded (20-99).
    * - Medium: A larger number of points are awarded (100-499).
    * - Large: A significant number of points are awarded (500-1999).
    * - Jackpot: The maximum number of points are awarded (2000-10000).
    * - ClippedByAnnualCap: Points are awarded but clipped by an annual cap, with both the awarded and would-have-been points provided.
    *
    * Each outcome type is represented as a record implementing the RewardOutcome interface.
*/

public sealed interface RewardOutcome
    permits RewardOutcome.NoReward, 
    RewardOutcome.Micro, 
    RewardOutcome.Small, 
    RewardOutcome.Medium, 
    RewardOutcome.Large, 
    RewardOutcome.Jackpot,
    RewardOutcome.ClippedByAnnualCap {

Points awardedPoints();

static RewardOutcome noReward() {
    return new NoReward("No reward");
}

static RewardOutcome microReward(int points) {
    if (points < 2 || points > 19) {
        throw new IllegalArgumentException("Micro range is [2,19]: " + points);
    }
    return new Micro(Points.of(points));
}

static RewardOutcome smallReward(int points) {
    if (points < 20 || points > 99) {
        throw new IllegalArgumentException("Small range is [20,99]: " + points);
    }
    return new Small(Points.of(points));
}

static RewardOutcome mediumReward(int points) {
    if (points < 100 || points > 499) {
        throw new IllegalArgumentException("Medium range is [100,499]: " + points);
    }
    return new Medium(Points.of(points));
}

static RewardOutcome largeReward(int points) {
    if (points < 500 || points > 1999) {
        throw new IllegalArgumentException("Large range is [500,1999]: " + points);
    }
    return new Large(Points.of(points));
}

static RewardOutcome jackpotReward(int points) {
    if (points < 2000 || points > 10000) {
        throw new IllegalArgumentException("Jackpot range is [2000,10000]: " + points);
    }
    return new Jackpot(Points.of(points));
}

static RewardOutcome clippedByAnnualCap(Points awarded, Points wouldHaveBeen) {
    return new ClippedByAnnualCap(awarded, wouldHaveBeen);
}

    record NoReward(String reason) implements RewardOutcome {
       public NoReward {
        Objects.requireNonNull(reason, "reason required");
            if(reason.isBlank()){
                throw new IllegalArgumentException("reason cannot be blank");
            }
        }

        @Override
        public Points awardedPoints() {
            return Points.ZERO;
        }
    }

    record Micro(Points points) implements RewardOutcome {
        public Micro {
            Objects.requireNonNull(points, "points required");
            if(points.value() < 2 || points.value() > 19){
                throw new IllegalArgumentException("Micro range is [2,19]: " + points.value());
            }
        }

        @Override
        public Points awardedPoints() {
            return points;
        }
    }
    
    record Small(Points points) implements RewardOutcome {

        public Small {
            Objects.requireNonNull(points, "points required");
            if(points.value() < 20 || points.value() > 99){
                throw new IllegalArgumentException("Small range is [20,99]: " + points.value());
            }
        }

        @Override
        public Points awardedPoints() {
            return points;
        }
    }

    record Medium(Points points) implements RewardOutcome {
        public Medium {
            Objects.requireNonNull(points, "points required");
            if(points.value() < 100 || points.value() > 499){
                throw new IllegalArgumentException("Medium range is [100,499]: " + points.value());
            }
        }

        @Override
        public Points awardedPoints() {
            return points;
        }
    }

    record Large(Points points) implements RewardOutcome {

        public Large {
            Objects.requireNonNull(points, "points required");
            if(points.value() < 500 || points.value() > 1999){
                throw new IllegalArgumentException("Large range is [500,999]: " + points.value());
            }
        }

        @Override
        public Points awardedPoints() {
            return points;
        }
    }

    record Jackpot(Points points) implements RewardOutcome {
        public Jackpot {
            Objects.requireNonNull(points, "points required");
            if(points.value() < 2000 || points.value() > 10000){
                throw new IllegalArgumentException("Jackpot range is [2000,10000]: " + points.value());
            }
        }

        @Override
        public Points awardedPoints() {
            return points;
        }
    }

    record ClippedByAnnualCap(Points awarded , Points wouldHaveBeen) implements RewardOutcome {   
        public ClippedByAnnualCap {
            Objects.requireNonNull(awarded, "awarded required");
            Objects.requireNonNull(wouldHaveBeen, "wouldHaveBeen required");
            if(awarded.value() < 1 || awarded.value() > 10000){
                throw new IllegalArgumentException("ClippedByAnnualCap points range is [1,10000]: " + awarded.value());
            }
            if(wouldHaveBeen.value() < 1 || wouldHaveBeen.value() > 10000){
                throw new IllegalArgumentException("ClippedByAnnualCap wouldHaveBeen range is [1,10000]: " + wouldHaveBeen.value());
            }
                if(awarded.value() > wouldHaveBeen.value()){
                    throw new IllegalArgumentException("awarded points must be <= wouldHaveBeen points");
                }
        }

        @Override
        public Points awardedPoints() {
            return awarded;
        }
    }
}

