package com.kalhan.post_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String id;
    private String firstname;
    private String lastname;
    private String profilePhoto;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<String> roles;
}
