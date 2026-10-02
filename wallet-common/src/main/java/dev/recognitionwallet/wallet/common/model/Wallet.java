package dev.recognitionwallet.wallet.common.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import java.time.temporal.ChronoUnit;
/**
 * Represents a wallet for an employee.
 * 
 * A wallet can be in one of two states: Active or Closed. 
 * An active wallet can receive and spend money, while a closed wallet cannot.
 * 
 * The wallet is identified by a unique UUID and is associated with an employee ID.
 * The wallet also has a creation timestamp and, if closed, a closure timestamp and reason.
 * 
 * Aggregate root for an employee's wallet.
 * 
 * Immutable  - every state transition(e.g., close) returns a new instance of the wallet.
 * The two-org 'enroll(String, Instant)' factory is preferred in tests for determinism;
 * the single-org defers to 'Instant.now().truncatedTo(ChronoUnit.MICROS)' for convience in production code.
 */

public record Wallet(
UUID id,
String employeeId,
WalletStatus status,
Instant createdAt
)
{

public Wallet {
    Objects.requireNonNull(id, "id required");
    Objects.requireNonNull(employeeId, "employeeId required");
    Objects.requireNonNull(status, "status required");
    Objects.requireNonNull(createdAt, "createdAt required");
    if(employeeId.isBlank()){
        throw new IllegalArgumentException("employeeId cannot be blank");
    }
}

public static Wallet enrollProduct(String employeeId, Instant createdAt){
    return new Wallet(UUID.randomUUID(), employeeId, WalletStatus.Active.INSTANCE, createdAt);
}

public static Wallet enrollProduct(String employeeId){
    return enrollProduct(employeeId, Instant.now().truncatedTo(ChronoUnit.MICROS));
}

public Wallet close(Instant closedAt, String reason){
    if(status instanceof WalletStatus.Closed){
        throw new IllegalStateException("Wallet is already closed");
    }
    return new Wallet(id, employeeId, new WalletStatus.Closed(closedAt, reason), createdAt);
}

public boolean isActive(){
    return status instanceof WalletStatus.Active;
}
}