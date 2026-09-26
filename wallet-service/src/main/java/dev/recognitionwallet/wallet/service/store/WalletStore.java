package dev.recognitionwallet.wallet.service.store;

import dev.recognitionwallet.wallet.common.model.Wallet;

import java.util.Optional;
import java.util.UUID;

//port for wallet persistence.

public interface WalletStore{

//persist a newly-enrolled wallet
//@throws WalletAlreadyExistException - if the employee already owns a wallet
Wallet save(Wallet wallet);

Optional<Wallet> findById(UUID id);


}