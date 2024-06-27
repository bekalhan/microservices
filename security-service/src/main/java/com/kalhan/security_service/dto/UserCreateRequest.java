package com.kalhan.security_service.dto;

import com.kalhan.security_service.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<User> followers;
}
