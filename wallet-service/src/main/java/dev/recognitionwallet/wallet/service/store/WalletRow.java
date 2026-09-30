package dev.recognitionwallet.wallet.service.store;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;
/**
 * One row of the 'wallets' table. Persistence shape only; the domain wallet stays annotation-free.
 * 
 * closedAt/closedReason are null unless status is CLOSED.
 * 
 */

@Table("wallets")
public record WalletRow(
@Id UUID id,
String employeeId,
String status,
Instant createdAt,
Instant closedAt,
String closedReason
){}