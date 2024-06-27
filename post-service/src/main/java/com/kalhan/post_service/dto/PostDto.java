package com.kalhan.post_service.dto;

import com.kalhan.post_service.entity.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
public class PostDto {
    private String title;
    private String thumbnail;
    private String userId;
    private String content;
    private Set<String> likes;
    private Set<String> saved;
    private List<Comment> comments;
}
