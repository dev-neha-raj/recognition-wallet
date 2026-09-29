package dev.recognitionwallet.wallet.common.model;

import java.time.Instant;
import java.util.Objects;
public sealed interface WalletStatus permits WalletStatus.Active, WalletStatus.Closed {
   
   record Active() implements WalletStatus {
    public static final Active INSTANCE = new Active();
   }

   record Closed(Instant closedAt, String reason) implements WalletStatus {
    public Closed {
        Objects.requireNonNull(closedAt, "closedAt required");
        Objects.requireNonNull(reason, "reason required");
        if(reason.isBlank()){
            throw new IllegalArgumentException("reason cannot be blank");
        }
    }
   }
}
