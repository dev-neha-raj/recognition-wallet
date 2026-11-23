package dev.recognitionwallet.wallet.service.ingest;

import dev.recognitionwallet.wallet.common.model.wallet;
import dev.recognitionwallet.wallet.service.store.WalletStore;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

//Eligibility checks before an event is stored: E1(active wallet) and E4(event freshness).
@Component
public class GrantPreflight{

static final Duration MAX_AGE = Duration.ofDays(7);
static final Duration MAX_CLOCK_SKEW = Duration.ofMinutes(5);

private final WalletStore wallets;
private final Clock clock;


public GrantPreflight(WalletStore wallets, Clock clock){
    this.wallets = wallets;
    this.clock = clock;
}

public void check(GrantSubmission submission){
    Instant now = clock.instant();
    if(submission.occurredAt().isBefore(now.minus(MAX_AGE))){
        throw new GrantNotEligibleException("EVENT_TOO_OLD");
    }
    if(submission.occurredAt().isAfter(now.plus(MAX_CLOCK_SKEW))){
        throw new GrantNotEligibleException("EVENT_IN_FUTURE");
    }
    boolean hasActiveWallet = wallets.findByEmployeeId(submission.employeeId())
        .map(Wallet::isActive)
        .orElse(false);
    if(!hasActiveWallet) {
        throw new GrantNotEligibleException("NO_ACTIVE_WALLET");
    }
}
}