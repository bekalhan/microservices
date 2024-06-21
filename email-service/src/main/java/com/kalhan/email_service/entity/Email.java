package com.kalhan.email_service.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Email {
    private String email;
    private String fullName;
    private String token;
}
