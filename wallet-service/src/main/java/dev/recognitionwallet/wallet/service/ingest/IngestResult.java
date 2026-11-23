package dev.recognitionwallet.wallet.service.ingest;

public record IngestResult(String sourceEventId, IngestStatus status){}