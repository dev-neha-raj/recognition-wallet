package dev.recognitionwallet.wallet.service.store;

import dev.recognitionwallet.wallet.service.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Raw-SQL tests of the wallets table constraints,
 * independent of the Java mapping code.
 */
@DataJdbcTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
class WalletSchemaConstraintsTest {

    private static final String INSERT = """
            INSERT INTO wallets (
                id,
                employee_id,
                status,
                created_at,
                closed_at,
                closed_reason
            )
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final Timestamp CREATED =
            Timestamp.from(
                    Instant.parse("2026-09-30T10:00:00Z")
            );

    private static final Timestamp CLOSED_AT =
            Timestamp.from(
                    Instant.parse("2026-10-01T10:00:00Z")
            );

    @Autowired
    JdbcTemplate jdbc;

    @Test
    void migrationsAppliedInOrder() {
        List<String> ids = jdbc.queryForList(
                "SELECT id FROM databasechangelog ORDER BY orderexecuted",
                String.class
        );

        assertThat(ids).containsExactly(
                "001-create-wallets",
                "002a-wallets-status-check",
                "002b-wallets-closed-fields-check"
        );
    }

    @Test
    void validActiveRowIsAccepted() {
        assertThatCode(() ->
                insert("emp-a", "ACTIVE", null, null)
        ).doesNotThrowAnyException();
    }

    @Test
    void validClosedRowIsAccepted() {
        assertThatCode(() ->
                insert(
                        "emp-c",
                        "CLOSED",
                        CLOSED_AT,
                        "left company"
                )
        ).doesNotThrowAnyException();
    }

@Test
void unknownStatusIsRejected() {
    assertThatThrownBy(() ->
            insert("emp-x", "SUSPENDED", null, null)
    )
            .isInstanceOf(DataIntegrityViolationException.class);
}

    @Test
    void activeRowWithCloseDetailsIsRejected() {
        assertThatThrownBy(() ->
                insert(
                        "emp-y",
                        "ACTIVE",
                        CLOSED_AT,
                        "should not be here"
                )
        )
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("chk_wallets_closed_fields");
    }

    @Test
    void closedRowWithoutReasonIsRejected() {
        assertThatThrownBy(() ->
                insert(
                        "emp-z",
                        "CLOSED",
                        CLOSED_AT,
                        null
                )
        )
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("chk_wallets_closed_fields");
    }

    private void insert(
            String employeeId,
            String status,
            Timestamp closedAt,
            String closedReason) {

        jdbc.update(
                INSERT,
                UUID.randomUUID(),
                employeeId,
                status,
                CREATED,
                closedAt,
                closedReason
        );
    }
}