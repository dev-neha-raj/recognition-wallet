package dev.recognitionwallet.wallet.api;

import java.time.Instant;
import java.util.UUID;

/*
response body for wallet endpoints

status is a string literal("ACTIVE" or "CLOSED") - not the sealed WalletStatus type
so the wire contract can evolve independently of the internal domain hierarchy.
Balance / YTD /redemption fields are deliberately absent is v1; they arrive in week 3+
once persistence and domain logic is in place to support them.
*/
public record WalletResponse(
UUID id,
String employeeId,
String status,
Instant createdAt
) {}