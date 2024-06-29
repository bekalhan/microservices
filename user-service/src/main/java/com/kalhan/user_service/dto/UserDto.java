package com.kalhan.user_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class UserDto {
    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private boolean isAccountLocked;
    private boolean enabled;
    private List<String> roles;
    private Set<UserDto> followers;
}
