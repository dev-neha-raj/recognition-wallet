package dev.recognitionwallet.wallet.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

/*
 * Boots the full application against the shared Testcontainers postgres. 
 passing proves Datasource, liquibase, and the single WalletStore bean are all wired together.
 */

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class WalletServiceApplicationTests {

    @Test
    void contextLoadsAndLiquibaseRuns() {
        // Successful context startup verifies:
        // Spring context + datasource + Liquibase wiring.
    }
}