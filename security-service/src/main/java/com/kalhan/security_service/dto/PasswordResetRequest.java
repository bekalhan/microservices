package com.kalhan.security_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PasswordResetRequest {
    private String firstname;
    private String lastname;
    @NotEmpty(message = "New password is mandatory")
    @NotNull(message = "New password is mandatory")
    @Size(min = 8, message = "New password should be 8 characters long minimum")
    private String newPassword;
    @NotEmpty(message = "Confirmation password is mandatory")
    @NotNull(message = "Confirmation password is mandatory")
    @Size(min = 8, message = "Confirmation password should be 8 characters long minimum")
    private String confirmationPassword;
    private String email;
    private String token;
}
