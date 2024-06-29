package com.kalhan.api_gateway.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
public class ValidationResponse {
    private String status;
    private boolean isAuthenticated;
    private String methodType;
    private String username;
    private String token;
    private String id;
    private List<String> authorities;
}