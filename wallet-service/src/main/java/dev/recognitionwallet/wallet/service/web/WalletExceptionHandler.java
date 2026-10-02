package dev.recognitionwallet.wallet.service.web;

import dev.recognitionwallet.wallet.service.store.WalletAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class WalletExceptionHandler {

    @ExceptionHandler(WalletAlreadyExistsException.class)
    ProblemDetail handleConflict(WalletAlreadyExistsException ex) {

        ProblemDetail pd =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.CONFLICT,
                        ex.getMessage());

        pd.setTitle("Wallet already exists");
        return pd;
    }

    @ExceptionHandler(WalletNotFoundException.class)
    ProblemDetail handleNotFound(WalletNotFoundException ex) {

        ProblemDetail pd =
                ProblemDetail.forStatusAndDetail(
                        HttpStatus.NOT_FOUND,
                        ex.getMessage());

        pd.setTitle("Wallet not found");
        return pd;
    }
}