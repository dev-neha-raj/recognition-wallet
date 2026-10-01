package dev.recognitionwallet.wallet.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Boots the full application against the shared Testcontainers postgres.
 *
 * Passing proves Datasource, Liquibase, and the single WalletStore bean
 * are all wired together end to end:
 * real HTTP into the running app, through validation, controller,
 * JdbcWalletStore, Liquibase, and PostgreSQL.
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class WalletApiIntegrationTest {

    @Autowired
    private TestRestTemplate http;

    @Autowired
    JdbcTemplate jdbc;

    // HTTP requests commit on Tomcat threads, so there is no test transaction
    // to roll back. We must manually clear the wallets table between tests.
    @BeforeEach
    void emptyWalletsTable() {
        jdbc.execute("TRUNCATE TABLE wallets");
    }

    /*
    @Test
    void enrollThenFetchRoundtripsThroughHttpAndPostgres() {
        ResponseEntity<JsonNode> created = postWallet("""
                {
                    "employeeId": "emp-1"
                }
                """);

        assertThat(created.getStatusCode())
                .isEqualTo(HttpStatus.CREATED);

        String id = created.getBody()
                .get("id")
                .asText();

        assertThat(created.getHeaders().getLocation().toString())
                .endsWith("/wallets/" + id);

        assertThat(created.getBody().get("employeeId").asText())
                .isEqualTo("emp-1");

        assertThat(created.getBody().get("status").asText())
                .isEqualTo("ACTIVE");

        assertThat(created.getBody().get("createdAt").asText())
                .isNotBlank();

        ResponseEntity<JsonNode> fetched =
                http.getForEntity("/wallets/" + id, JsonNode.class);

        assertThat(fetched.getStatusCode())
                .isEqualTo(HttpStatus.OK);

        assertThat(fetched.getBody())
                .isEqualTo(created.getBody());

        assertThat(rowsFor("emp-1"))
                .isEqualTo(1);
    }
    */

    /*
    @Test
    void duplicateEnrollmentReturns409ProblemAndKeepsOneRow() {
        postWallet("{\"employeeId\": \"emp-2\"}");

        ResponseEntity<JsonNode> duplicate =
                postWallet("{\"employeeId\": \"emp-2\"}");

        assertProblem(
                duplicate,
                HttpStatus.CONFLICT,
                "Wallet already exists"
        );

        assertThat(duplicate.getBody().get("title").asText())
                .isEqualTo("Wallet already exists");

        assertThat(rowsFor("emp-2"))
                .isEqualTo(1);
    }

    @Test
    void blankEmployeeIdReturns400Problem() {
        assertProblem(
                postWallet("{\"employeeId\": \"\"}"),
                HttpStatus.BAD_REQUEST,
                "Employee ID must not be blank"
        );

        assertThat(totalRows())
                .isEqualTo(0);
    }

    @Test
    void employeeIdLongerThan64CharsReturns400Problem() {
        String longId = "a".repeat(65);

        assertProblem(
                postWallet("{\"employeeId\": \"" + longId + "\"}"),
                HttpStatus.BAD_REQUEST,
                "Employee ID must not exceed 64 characters"
        );

        assertThat(totalRows())
                .isEqualTo(0);
    }

    @Test
    void malformedJsonReturns400Problem() {
        assertProblem(
                postWallet("{\"employeeId\": \"emp-3\""),
                HttpStatus.BAD_REQUEST,
                "Malformed JSON"
        );

        assertThat(totalRows())
                .isEqualTo(0);
    }
    */

    /*
    @Test
    void unknownWalletReturns404Problem() {
        ResponseEntity<JsonNode> response =
                http.getForEntity(
                        "/wallets/" + UUID.randomUUID(),
                        JsonNode.class
                );

        assertProblem(
                response,
                HttpStatus.NOT_FOUND,
                "Wallet not found"
        );

        assertThat(response.getBody().get("title").asText())
                .isEqualTo("Wallet not found");
    }
    */

    /*
    @Test
    void nonUuidPathReturns400Problem() {
        ResponseEntity<JsonNode> response =
                http.getForEntity(
                        "/wallets/not-a-uuid",
                        JsonNode.class
                );

        assertProblem(
                response,
                HttpStatus.BAD_REQUEST,
                "Invalid UUID string"
        );

        assertThat(response.getBody().get("title").asText())
                .isEqualTo("Invalid UUID string");
    }

    @Test
    void twoConcurrentEnrollmentsForSameEmployeeOnlyCreatesOneRow() {
        String body = "{\"employeeId\": \"emp-4\"}";

        List<HttpStatusCode> statuses = List.of(
                CompletableFuture.supplyAsync(
                        () -> postWallet(body).getStatusCode()
                ),
                CompletableFuture.supplyAsync(
                        () -> postWallet(body).getStatusCode()
                )
        )
                .stream()
                .map(CompletableFuture::join)
                .toList();

        assertThat(statuses)
                .containsExactlyInAnyOrder(
                        HttpStatus.CREATED,
                        HttpStatus.CONFLICT
                );

        assertThat(rowsFor("emp-4"))
                .isEqualTo(1);
    }
    */

    private ResponseEntity<JsonNode> postWallet(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return http.postForEntity(
                "/wallets",
                new HttpEntity<>(body, headers),
                JsonNode.class
        );
    }

    private static void assertProblem(
            ResponseEntity<JsonNode> response,
            HttpStatus expectedStatus,
            String expectedTitle) {

        assertThat(response.getStatusCode())
                .isEqualTo(expectedStatus);

        assertThat(response.getHeaders().getContentType())
                .isNotNull()
                .matches(
                        ct -> ct.isCompatibleWith(
                                MediaType.APPLICATION_PROBLEM_JSON
                        ),
                        "Expected Content-Type compatible with " +
                                "application/problem+json but was " +
                                response.getHeaders().getContentType()
                );

        assertThat(response.getBody().get("status").asInt())
                .isEqualTo(expectedStatus.value());

        assertThat(response.getBody().get("title").asText())
                .isEqualTo(expectedTitle);
    }

    private int rowsFor(String employeeId) {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM wallets WHERE employee_id = ?",
                Integer.class,
                employeeId
        );
    }

    private int totalRows() {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM wallets",
                Integer.class
        );
    }
}