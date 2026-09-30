package dev.recognitionwallet.wallet.service.store;

import dev.recognitionwallet.wallet.common.model.Wallet;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory WalletStore. Not a spring bean since M8; kept as a fast fake for tests;
 * 
 * Two maps: one by wallet id, one by employeeId(the uniquesness index). Enrollment claims the 
 * employee slot with putIfAbsent, so a race between two identical saves yields exactly one winner.
 * 
 */


public class InMemoryWalletStore implements WalletStore{

private final ConcurrentHashMap<UUID, Wallet> byId = new ConcurrentHashMap<>();
private final ConcurrentHashMap<String, UUID> employeeIndex = new ConcurrentHashMap<>();

@Override
public Wallet save(Wallet wallet){
UUID alreadyClaimed = employeeIndex.putIfAbsent(wallet.employeeId(), wallet.id());
if(alreadyClaimed != null){
    throw new WalletAlreadyExistsException(wallet.employeeId());
}
byId.put(wallet.id(), wallet);
return wallet;
}

@Override
public Optional<Wallet> findById(UUID id){
    return Optional.ofNullable(byId.get(id));
}
}