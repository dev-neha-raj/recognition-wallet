package dev.recognitionwallet.wallet.service.web;

import dev.recognitionwallet.wallet.api.WalletResponse;
import dev.recognitionwallet.wallet.common.model.Wallet;
import dev.recognitionwallet.wallet.common.model.WalletStatus;
/**
 * Maps the domain wallet (wallet-common) to the wire WalletResponse(wallet-api).
 * 
 * This class is the seam of the hexagon. The switch is exhaustive over the sealed WalletStatus
 * with NO default branch - add a new status variant in wallet-common and the compiler forces
 * you back here to decide its wire representation
 * 
 */

final class WalletMapper{

private WalletMapper(){}

static WalletResponse toResponse(Wallet wallet){
    String status = switch(wallet.status()){
        case WalletStatus.Active a -> "ACTIVE";
        case WalletStatus.Closed c -> "CLOSED";
    };
    return new WalletResponse(wallet.id(), wallet.employeeId(), status, wallet.createdAt());
}

}


