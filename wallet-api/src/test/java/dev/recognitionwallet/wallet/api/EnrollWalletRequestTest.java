package dev.recognitionwallet.wallet.api;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class EnrollWalletRequestTest {

    private static final Validator VALIDATOR =
            Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validEmployeeIdHasNoViolations() {
        var request = new EnrollWalletRequest("emp-1");

        Set<ConstraintViolation<EnrollWalletRequest>> violations =
                VALIDATOR.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void nullEmployeeIdViolatesNotBlank() {
        var request = new EnrollWalletRequest(null);

        Set<ConstraintViolation<EnrollWalletRequest>> violations =
                VALIDATOR.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("blank");
    }

    @Test
    void emptyEmployeeIdViolatesConstraints() {
        var request = new EnrollWalletRequest("");

        Set<ConstraintViolation<EnrollWalletRequest>> violations =
                VALIDATOR.validate(request);

        assertThat(violations).isNotEmpty();

        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .anyMatch(message -> message.contains("blank"));
    }

    @Test
    void whitespaceOnlyEmployeeIdViolatesNotBlank() {
        var request = new EnrollWalletRequest(" ");

        Set<ConstraintViolation<EnrollWalletRequest>> violations =
                VALIDATOR.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("blank");
    }

    @Test
    void exactly64CharactersIsValid() {
        var request = new EnrollWalletRequest("a".repeat(64));

        Set<ConstraintViolation<EnrollWalletRequest>> violations =
                VALIDATOR.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    void moreThan64CharactersViolatesSize() {
        var request = new EnrollWalletRequest("a".repeat(65));

        Set<ConstraintViolation<EnrollWalletRequest>> violations =
                VALIDATOR.validate(request);

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("characters");
    }
}