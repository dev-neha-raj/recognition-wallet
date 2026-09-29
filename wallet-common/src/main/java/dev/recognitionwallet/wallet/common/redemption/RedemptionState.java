package dev.recognitionwallet.wallet.common.redemption;

import java.time.Instant;
import java.util.Objects;

/*
sealed hierarchy representing the state of a redemption request in the wallet system.
The states include:
- Requested: The redemption request has been made and is awaiting approval.
- Approved: The redemption request has been approved and is awaiting settlement.
- Rejected: The redemption request has been rejected, with a reason provided.
- Settled: The redemption request has been successfully settled, with a bank reference provided.
- Failed: The redemption request has failed, with a reason provided.
- Reversed: The redemption request has been reversed, with a reason provided.
Each state is represented as a record implementing the RedemptionState interface.
*/ 
public sealed interface RedemptionState permits 
    RedemptionState.Requested, 
    RedemptionState.Approved, 
    RedemptionState.Rejected,
    RedemptionState.Settled,
    RedemptionState.Failed,
    RedemptionState.Reversed {

Instant timestamp();

    record Requested(Instant timestamp) implements RedemptionState {
        public Requested {
            Objects.requireNonNull(timestamp, "timestamp required");
        }
        public Instant requestedAt() {
            return timestamp;
        }
        public static Requested of(Instant timestamp) {
            return new Requested(timestamp);
        }
    }
    record Approved(Instant timestamp) implements RedemptionState {
        public Approved {
            Objects.requireNonNull(timestamp, "timestamp required");
        }
        public Instant approvedAt() {
            return timestamp;
        }
    }

    record Rejected(Instant timestamp, String reason) implements RedemptionState {
        public Rejected {
            Objects.requireNonNull(timestamp, "timestamp required");
            Objects.requireNonNull(reason, "reason required");
            if(reason.isBlank()){
                throw new IllegalArgumentException("reason cannot be blank");
        }
        }
        public Instant rejectedAt() {
            return timestamp;
        }
    }

    record Settled(Instant timestamp, String bankReference) implements RedemptionState {
        public Settled {
            Objects.requireNonNull(timestamp, "timestamp required");
            Objects.requireNonNull(bankReference, "bank reference required");
            if(bankReference.isBlank()){
                throw new IllegalArgumentException("bank reference cannot be blank");
            }
        }
        public Instant settledAt() {
            return timestamp;
        }
    }

    record Failed(Instant timestamp, String reason) implements RedemptionState {
        public Failed {
            Objects.requireNonNull(timestamp, "timestamp required");
            Objects.requireNonNull(reason, "reason required");
            if(reason.isBlank()){
                throw new IllegalArgumentException("reason cannot be blank");
            }
        }
        public Instant failedAt() {
            return timestamp;
        }
    }

    record Reversed(Instant timestamp, String reason) implements RedemptionState {
        public Reversed {
            Objects.requireNonNull(timestamp, "timestamp required");
            Objects.requireNonNull(reason, "reason required");
            if(reason.isBlank()){
                throw new IllegalArgumentException("reason cannot be blank");
            }
        }
        public Instant reversedAt() {
            return timestamp;
        }
    }
}
