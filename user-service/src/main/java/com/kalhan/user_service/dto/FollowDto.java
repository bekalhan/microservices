package com.kalhan.user_service.dto;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowDto {
    private String firstname;
    private String lastname;
    private String profilePhoto;
}
