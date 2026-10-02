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
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

/// End-to-end: real HTTP into the running app, through validation, controller, JdbcWalletStore,
/// and a real Postgres, and back out as status codes and JSON.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class WalletApiIntegrationTest {

    @Autowired TestRestTemplate http;
    @Autowired JdbcTemplate jdbc;

    // HTTP requests commit on Tomcat threads, so there is no test transaction to roll back.
    @BeforeEach
    void emptyWalletsTable() {
        jdbc.execute("TRUNCATE TABLE wallets");
    }

    @Test
    void enrollThenFetchRoundtripsThroughHttpAndPostgres() {
        ResponseEntity<JsonNode> created = postWallet("""
                {
                    "employeeId": "emp-1"
                }
                """);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String id = created.getBody().get("id").asText();
        assertThat(created.getHeaders().getLocation()).hasToString("/wallets/" + id);
        assertThat(created.getBody().get("employeeId").asText()).isEqualTo("emp-1");
        assertThat(created.getBody().get("status").asText()).isEqualTo("ACTIVE");
        assertThat(created.getBody().get("createdAt").asText()).isNotBlank();

        ResponseEntity<JsonNode> fetched = http.getForEntity("/wallets/" + id, JsonNode.class);
        assertThat(fetched.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(fetched.getBody()).isEqualTo(created.getBody());

        assertThat(rowsFor("emp-1")).isEqualTo(1);
    }

    @Test
    void duplicateEnrollmentReturns409ProblemAndKeepsOneRow() {
        postWallet("{\"employeeId\":\"emp-2\"}");

        ResponseEntity<JsonNode> duplicate = postWallet("{\"employeeId\":\"emp-2\"}");

        assertProblem(duplicate, HttpStatus.CONFLICT);
        assertThat(duplicate.getBody().get("title").asText()).isEqualTo("Wallet already exists");
        assertThat(rowsFor("emp-2")).isEqualTo(1);
    }

    @Test
    void blankEmployeeIdReturns400Problem() {
        assertProblem(postWallet("{\"employeeId\":\"  \"}"), HttpStatus.BAD_REQUEST);
        assertThat(totalRows()).isZero();
    }

    @Test
    void employeeIdLongerThan64CharsReturns400Problem() {
        String tooLong = "a".repeat(65);
        assertProblem(postWallet("{\"employeeId\":\"" + tooLong + "\"}"), HttpStatus.BAD_REQUEST);
        assertThat(totalRows()).isZero();
    }

    @Test
    void malformedJsonReturns400Problem() {
        assertProblem(postWallet("{not json}"), HttpStatus.BAD_REQUEST);
    }

    @Test
    void unknownWalletReturns404Problem() {
        ResponseEntity<JsonNode> response = http.getForEntity(
                "/wallets/11111111-1111-1111-1111-111111111111", JsonNode.class);

        assertProblem(response, HttpStatus.NOT_FOUND);
        assertThat(response.getBody().get("title").asText()).isEqualTo("Wallet not found");
    }

    @Test
    void nonUuidPathReturns400Problem() {
        assertProblem(http.getForEntity("/wallets/not-a-uuid", JsonNode.class), HttpStatus.BAD_REQUEST);
    }

    @Test
    void twoConcurrentEnrollmentsForSameEmployeeYieldOne201AndOne409() {
        String body = "{\"employeeId\":\"emp-race\"}";

        List<HttpStatusCode> statuses = List.of(
                CompletableFuture.supplyAsync(() -> postWallet(body).getStatusCode()),
                CompletableFuture.supplyAsync(() -> postWallet(body).getStatusCode())
        ).stream()
         .map(CompletableFuture::join)
         .toList();

        assertThat(statuses).containsExactlyInAnyOrder(HttpStatus.CREATED, HttpStatus.CONFLICT);
        assertThat(rowsFor("emp-race")).isEqualTo(1);
    }

    private ResponseEntity<JsonNode> postWallet(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return http.postForEntity("/wallets", new HttpEntity<>(json, headers), JsonNode.class);
    }

    private static void assertProblem(ResponseEntity<JsonNode> response, HttpStatus expected) {
        assertThat(response.getStatusCode()).isEqualTo(expected);
        assertThat(response.getHeaders().getContentType())
                .isNotNull()
                .matches(ct -> ct.isCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        "Content-Type application/problem+json");
        assertThat(response.getBody().get("status").asInt()).isEqualTo(expected.value());
    }

    private int rowsFor(String employeeId) {
        return jdbc.queryForObject(
                "SELECT count(*) FROM wallets WHERE employee_id = ?", Integer.class, employeeId);
    }

    private int totalRows() {
        return jdbc.queryForObject("SELECT count(*) FROM wallets", Integer.class);
    }
}