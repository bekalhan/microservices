package com.kalhan.post_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kalhan.post_service.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;


@Getter
@Setter
@Builder
public class CommentDto {
    @JsonIgnore
    private String userId;
    private String content;
    private Set<UserDto> likes;
    private UserDto userDto;
}
