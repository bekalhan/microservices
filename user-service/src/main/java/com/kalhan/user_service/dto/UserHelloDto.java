package com.kalhan.user_service.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "user-info")
@Getter
@Setter
public class UserHelloDto {
    private String message;
}
