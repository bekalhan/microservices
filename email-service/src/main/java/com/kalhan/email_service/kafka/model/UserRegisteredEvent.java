package com.kalhan.email_service.kafka.model;

import com.kalhan.email_service.entity.Email;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRegisteredEvent {
    private String email;
    private String firstname;
    private String lastname;
    private String token;

    public static Email getEmailEntityFromEvent(UserRegisteredEvent event){
        return Email.builder()
                .email(event.getEmail())
                .fullName(event.getFirstname()+event.getLastname())
                .token(event.token).build();
    }
}
