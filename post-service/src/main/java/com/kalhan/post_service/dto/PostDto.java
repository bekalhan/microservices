package com.kalhan.post_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kalhan.post_service.entity.Comment;
import lombok.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private String title;
    private String thumbnail;
    @JsonIgnore
    private String userId;
    private String content;
    private Set<UserDto> likes;
    private Set<UserDto> saved;
    private List<CommentDto> comments;
    private UserDto userDto;
}
