package dev.recognitionwallet.wallet.service.ingest;

/// The grant is well-formed but can't be accepted(no active wallet, too old, in the future). Maps to HTTP 422

public class GrantNotEligibleException extends RuntimeException {

private final String reason;

public GrantNotEligibleException(String reason){
    super("Grant not eligible:" +reason);
}

public String reason(){
    return reason;
}

}