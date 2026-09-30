package dev.recognitionwallet.wallet.service.store;

import dev.recognitionwallet.wallet.common.model.Wallet;
import dev.recognitionwallet.wallet.common.model.WalletStatus;

/**
 * converts between the domain wallet and the persistence WalletRow
 * 
 * Both switches are exhaustive with no default: a new WalletStatus variant will not compile
 * util this class decides how to store and load it
 * 
 */


final class WalletRowMapper{

static final String ACTIVE = "ACTIVE";
static final String CLOSED = "CLOSED";

private WalletRowMapper() {}

    static WalletRow toRow(Wallet wallet){
        return switch (wallet.status()){
            case WalletStatus.Active a -> new WalletRow(
                wallet.id(), wallet.employeeId(), ACTIVE, wallet.createdAt(), null, null );
            case WalletStatus.Closed c -> new WalletRow(
                wallet.id(), wallet.employeeId(), CLOSED, wallet.createdAt(), c.closedAt(), c.reason());            
        };
    }

    static Wallet toDomain(WalletRow row){
        WalletStatus status = switch (row.status()){
            case ACTIVE -> WalletStatus.Active.INSTANCE;
            case CLOSED -> new WalletStatus.Closed(row.closedAt(), row.closedReason());
            default -> throw new IllegalStateException(
                "Unknown wallet status in database: " + row.status() + "(id" + row.id() + ")");
        };
        return new Wallet(row.id(), row.employeeId(), status, row.createdAt());
    }
}
