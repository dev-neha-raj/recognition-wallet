package dev.recognitionwallet.wallet.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WalletResponseTest {

    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    private static final UUID ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    private static final Instant CREATED_AT =
            Instant.parse("2026-09-07T14:32:00Z");

    @Test
    void accessorsReturnValuesPassedToConstructor() {
        var response =
                new WalletResponse(ID, "emp-1", "ACTIVE", CREATED_AT);

        assertThat(response.id()).isEqualTo(ID);
        assertThat(response.employeeId()).isEqualTo("emp-1");
        assertThat(response.status()).isEqualTo("ACTIVE");
        assertThat(response.createdAt()).isEqualTo(CREATED_AT);
    }

    @Test
    void jsonSerializationHasExpectedShape() throws Exception {
        var response =
                new WalletResponse(ID, "emp-1", "ACTIVE", CREATED_AT);

        String json = MAPPER.writeValueAsString(response);

        assertThat(json)
                .contains("\"id\":\"00000000-0000-0000-0000-000000000001\"");

        assertThat(json)
                .contains("\"employeeId\":\"emp-1\"");

        assertThat(json)
                .contains("\"status\":\"ACTIVE\"");

        assertThat(json)
                .contains("\"createdAt\":\"2026-09-07T14:32:00Z\"");
    }

    @Test
    void jsonRoundtripPreservesAllFields() throws Exception {
        var original =
                new WalletResponse(ID, "emp-1", "ACTIVE", CREATED_AT);

        String json = MAPPER.writeValueAsString(original);

        WalletResponse roundtripped =
                MAPPER.readValue(json, WalletResponse.class);

        assertThat(roundtripped).isEqualTo(original);
    }
}