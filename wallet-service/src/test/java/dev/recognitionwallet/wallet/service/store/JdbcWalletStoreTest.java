package dev.recognitionwallet.wallet.service.store;

import dev.recognitionwallet.wallet.common.model.Wallet;
import dev.recognitionwallet.wallet.common.model.WalletStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JdbcWalletStore.class)
class JdbcWalletStoreTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16");

    @Autowired
    JdbcWalletStore store;

    @Autowired
    WalletRepository repository;

    private static final Instant CREATED =
            Instant.parse("2026-09-29T10:00:00Z");

    @Test
    void savedActiveWalletRoundtripsThroughPostgres() {
        Wallet wallet = Wallet.enrollProduct("emp-1", CREATED);

        store.save(wallet);

        assertThat(store.findById(wallet.id()))
                .contains(wallet);
    }

    @Test
    void savedClosedWalletRoundtripsWithCloseDetails() {
        Wallet closed = Wallet.enrollProduct("emp-2", CREATED)
                .close(
                        Instant.parse("2026-09-29T10:00:00Z"),
                        "left company"
                );

        store.save(closed);

        Wallet loaded = store.findById(closed.id())
                .orElseThrow();

        assertThat(loaded).isEqualTo(closed);
        assertThat(loaded.status())
                .isInstanceOf(WalletStatus.Closed.class);
    }

    @Test
    void findByUnknownIdReturnsEmpty() {
        assertThat(store.findById(UUID.randomUUID()))
                .isEmpty();
    }
/**
 @Test
void secondWalletForSameEmployeeIsRejectedByDatabase() {
    store.save(Wallet.enrollProduct("emp-3", CREATED));

    assertThatThrownBy(() ->
            store.save(Wallet.enrollProduct("emp-3", CREATED))
    )
            .isInstanceOf(WalletAlreadyExistsException.class)
            .hasMessageContaining("emp-3");
}
**/


    @Test
    void rowIsStoredWithExpectedColumnValues() {
        Wallet wallet = Wallet.enrollProduct("emp-4", CREATED);

        store.save(wallet);

        WalletRow row = repository
                .findByEmployeeId("emp-4")
                .orElseThrow();

        assertThat(row.id()).isEqualTo(wallet.id());
        assertThat(row.status()).isEqualTo("ACTIVE");
        assertThat(row.createdAt()).isEqualTo(CREATED);
        assertThat(row.closedAt()).isNull();
        assertThat(row.closedReason()).isNull();
    }
}