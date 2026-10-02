package dev.recognitionwallet.wallet.service.web;

import dev.recognitionwallet.wallet.api.EnrollWalletRequest;
import dev.recognitionwallet.wallet.api.WalletResponse;
import dev.recognitionwallet.wallet.common.model.Wallet;
import dev.recognitionwallet.wallet.service.store.WalletStore;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

/**
 * HTTP boundary for wallet enrollment and lookup
 * 
 * The seam of the hexagon: accept a wire DTO(wallet-api), drive the domain(wallet-common) via
 * 
 * Wallet.enroll, persist through the walletStore port, map the domain result back to a wire DTO.
 * No business logix lives here - the domain owns rules, the store owns persistence.
 * 
 */


@RestController
@RequestMapping("/wallets")
public class WalletController{

private final WalletStore store;

public WalletController(WalletStore store){
    this.store=store;
}

@PostMapping
public ResponseEntity<WalletResponse> enroll(
        @Valid @RequestBody EnrollWalletRequest request) {

    Wallet wallet = Wallet.enrollProduct(request.employeeId());

    Wallet saved = store.save(wallet); // throws WalletAlreadyExistsException -> 409

    WalletResponse body = WalletMapper.toResponse(saved);

    URI location = URI.create("/wallets/" + saved.id());

    return ResponseEntity.created(location).body(body);
}

@GetMapping("/{id}")
public WalletResponse getById(@PathVariable UUID id){
    Wallet wallet = store.findById(id)
        .orElseThrow(() -> new WalletNotFoundException(id));
    return WalletMapper.toResponse(wallet);
}
}