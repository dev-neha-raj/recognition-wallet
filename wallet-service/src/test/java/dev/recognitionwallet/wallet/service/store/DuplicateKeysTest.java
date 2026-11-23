package dev.recognitionwallet.wallet.service.store;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicateKeysTest {

    @Test
    void detectsDirectDuplicateKey() {
        assertThat(DuplicateKeys.isDuplicateKey(
                new DuplicateKeyException("dup")))
                .isTrue();
    }

    @Test
    void detectsDuplicateKeyNestedInCauses() {
        var wrapped = new RuntimeException(
                "outer",
                new IllegalStateException(
                        "middle",
                        new DuplicateKeyException("dup")));

        assertThat(DuplicateKeys.isDuplicateKey(wrapped))
                .isTrue();
    }

    @Test
    void otherConstraintViolationsAreNotDuplicates() {
        assertThat(DuplicateKeys.isDuplicateKey(
                new DataIntegrityViolationException("check failed")))
                .isFalse();
    }

    @Test
    void nullIsNotADuplicate() {
        assertThat(DuplicateKeys.isDuplicateKey(null))
                .isFalse();
    }
}