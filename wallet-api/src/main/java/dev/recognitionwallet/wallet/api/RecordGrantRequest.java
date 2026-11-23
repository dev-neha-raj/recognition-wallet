
package dev.recognitionwallet.wallet.api;

import java.time.Instant;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * Request body for POST /grant: one AI-usage event from a source tool
 * 
 *The three outcome metrics may be null. An event missing them is still accepted; the rule filter 
 * then records it as FILTER_FAILED: INCOMPLETE_OUTCOME instead of rewarding it.
 * 
 */

public record RecordGrantRequest(

@NotBlank(message = "sourceEVentId must not be blank")
@Size(max=128, message = "sourceEventId must be at most 128 characters")
String sourceEventId,

@NotBlank(message = "employeeId must not be blank")
@Size(max=64, message = "employeeId must be at most 64 characters")
String employeeId,

@NotBlank(message = "sourceTool must not be blank")
@Size(max=64, message = "sourceTool must be at most 64 characters")
String sourceTool,

@NotBlank(message = "eventKind must not be blank")
@Size(max=64, message = "sourceeventKindEventId must be at most 128 characters")
String eventKind,

@NotNull(message = "occurredAt is required")
Instant occurredAt,

@NotNull(message = "promptText is required")
@Size(max=10000, message = "promptText must be at most 10000 characters")
String promptText,

@DecimalMin(value = "0.0", message ="acceptanceRatio must be between 0 and 1")
@DecimalMax(value = "1.0", message ="acceptanceRatio must be between 0 and 1")
Double acceptanceRatio,

@PositiveOrZero(message = "followUpCount must not be negative")
Integer followUpCount,

@PositiveOrZero(message = "timeToAcceptaceSeconds must not be negative")
Long timeToAcceptaceSeconds
){}