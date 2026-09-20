package dev.recognitionwallet.wallet.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/*
    * EnrollWalletRequest is a record that represents the request payload for enrolling a wallet.
    * It contains the employee ID of the user who wants to enroll their wallet.
    *
    * @param employeeId The employee ID of the user enrolling their wallet. It must not be blank and must contain at least one character.
*/

public record EnrollWalletRequest(
@NotBlank(message = "Employee ID cannot be blank")
@Size(min = 1,max = 64, message = "Employee ID must contain between 1 and 64 characters")              
String employeeId
) {}