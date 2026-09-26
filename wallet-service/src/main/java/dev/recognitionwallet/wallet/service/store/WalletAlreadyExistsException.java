package dev.recognitionwallet.wallet.service.store;

//Thrown when enrolling an employee who already owns a wallet. Maps to HTTP 409
public class WalletAlreadyExistsException extends RuntimeException {

private final String employeeId;

public WalletAlreadyExistsException(String employeeId){

    super("Wallet already exists for employeeId: " + employeeId);
    this.employeeId = employeeId;
}

public String employeeId(){
    return employeeId;
}
}