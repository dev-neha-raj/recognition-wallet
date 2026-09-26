package dev.recognitionwallet.wallet.service.store;

import dev.recognitionwallet.wallet.common.model.Wallet;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryWalletStoreTest {

    private final InMemoryWalletStore store = new InMemoryWalletStore();

    @Test
    void savedWalletCanBeFoundById() {

        Wallet wallet = Wallet.enrollProduct(
                "emp-1",
                Instant.parse("2026-09-20T10:00:00Z")
        );

        store.save(wallet);

        Optional<Wallet> found = store.findById(wallet.id());

        assertThat(found).contains(wallet);
    }

    @Test
    void findByUnknownIdReturnsEmpty() {

        assertThat(store.findById(UUID.randomUUID())).isEmpty();
    }

    @Test
    void savingSecondWalletForSameEmployeeThrowsConflict() {

        store.save(Wallet.enrollProduct("emp-1"));

        assertThatThrownBy(() ->
                store.save(Wallet.enrollProduct("emp-1"))
        )
        .isInstanceOf(WalletAlreadyExistsException.class)
        .hasMessageContaining("emp-1");
    }

    @Test
    void differentEmployeesCanBothEnroll() {

        Wallet a = store.save(Wallet.enrollProduct("emp-1"));
        Wallet b = store.save(Wallet.enrollProduct("emp-2"));

        assertThat(store.findById(a.id())).contains(a);
        assertThat(store.findById(b.id())).contains(b);
    }
}