package dev.recognitionwallet.wallet.service.web;

import java.util.UUID;

//Thrown when GET/wallets/{id} references an unknown wallet. Maps to HTTP 404.
public class WalletNotFoundException extends RuntimeException{
    public WalletNotFoundException(UUID id){
        super("Wallet not found " + id);
    }
}