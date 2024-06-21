package com.kalhan.email_service.kafka.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kafka.topics.user-registered")
@Getter
@Setter
public class UserRegisteredConsumerProperties {
    private String topic;
    private String consumerGroup;
}
