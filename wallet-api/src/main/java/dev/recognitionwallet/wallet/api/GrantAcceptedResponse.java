package dev.recognitionwallet.wallet.api;

//Response body for POST /grants (always 202). status is 'ACCEPTED' or 'DUPLICATE'
public record GrantAcceptedResponse(String sourceEventId, String status){}