package com.kalhan.security_service.dto;

import com.kalhan.security_service.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UserCreateRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private boolean isAccountLocked;
    private boolean enabled;
    private List<String> roles;
    private String token;
}
