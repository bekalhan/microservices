package com.kalhan.user_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChangePasswordRequest {
    @NotEmpty(message = "Current password is mandatory")
    @NotNull(message = "Current password is mandatory")
    @Size(min = 8, message = "Current password should be 8 characters long minimum")
    private String currentPassword;
    @NotEmpty(message = "New password is mandatory")
    @NotNull(message = "New password is mandatory")
    @Size(min = 8, message = "New password should be 8 characters long minimum")
    private String newPassword;
    @NotEmpty(message = "Confirmation password is mandatory")
    @NotNull(message = "Confirmation password is mandatory")
    @Size(min = 8, message = "Confirmation password should be 8 characters long minimum")
    private String confirmationPassword;
}