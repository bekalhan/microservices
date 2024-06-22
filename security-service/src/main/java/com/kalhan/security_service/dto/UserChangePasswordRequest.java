package com.kalhan.security_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserChangePasswordRequest {
    private String id;
    private String newPassword;
}
