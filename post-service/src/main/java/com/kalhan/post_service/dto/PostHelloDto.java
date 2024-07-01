package com.kalhan.post_service.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "post-info")
@Getter
@Setter
public class PostHelloDto {
    private String message;
}
