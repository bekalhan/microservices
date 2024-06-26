package com.kalhan.user_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Builder
public class FollowDto {
    private String firstname;
    private String lastname;
    private String profilePhoto;
}
