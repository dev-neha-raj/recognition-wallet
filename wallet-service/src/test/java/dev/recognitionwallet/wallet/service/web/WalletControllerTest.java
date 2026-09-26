package dev.recognitionwallet.wallet.service.web;

import dev.recognitionwallet.wallet.common.model.Wallet;
import dev.recognitionwallet.wallet.common.model.WalletStatus;
import dev.recognitionwallet.wallet.service.store.WalletAlreadyExistsException;
import dev.recognitionwallet.wallet.service.store.WalletStore;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(WalletController.class)

class WalletControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    WalletStore store;

    private static final UUID ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    private static final Instant CREATED =
            Instant.parse("2026-09-20T10:00:00Z");

    private static Wallet activeWallet() {
        return new Wallet(
                ID,
                "emp-1",
                WalletStatus.Active.INSTANCE,
                CREATED
        );
    }

    @Test
    void postValidRequestReturns201WithLocationAndBody() throws Exception {

        when(store.save(any())).thenReturn(activeWallet());

        mvc.perform(post("/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\":\"emp-1\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "/wallets/" + ID
                ))
                .andExpect(jsonPath("$.id").value(ID.toString()))
                .andExpect(jsonPath("$.employeeId").value("emp-1"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-09-20T10:00:00Z"));
    }

    @Test
    void postBlankEmployeeIdReturn400() throws Exception {

        mvc.perform(post("/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postDuplicateEmployeeReturn409() throws Exception {

        when(store.save(any()))
                .thenThrow(new WalletAlreadyExistsException("emp-1"));

        mvc.perform(post("/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"employeeId\":\"emp-1\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void getExistingWalletReturns200() throws Exception {

        when(store.findById(ID))
                .thenReturn(Optional.of(activeWallet()));

        mvc.perform(get("/wallets/" + ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("emp-1"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getUnknownWalletReturns404() throws Exception {

        when(store.findById(any()))
                .thenReturn(Optional.empty());

        mvc.perform(get("/wallets/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
}