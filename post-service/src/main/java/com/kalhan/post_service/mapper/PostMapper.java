package com.kalhan.post_service.mapper;

import com.kalhan.post_service.dto.PostDto;
import com.kalhan.post_service.dto.UserDto;
import com.kalhan.post_service.entity.Post;
import com.kalhan.post_service.service.UserApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostMapper {

    private final UserApiClient userApiClient;
    private final CommentMapper commentMapper;

    public Post toPost(PostDto postDto) {
        return Post.builder()
                .title(postDto.getTitle())
                .userId(postDto.getUserId())
                .thumbnail(postDto.getThumbnail())
                .content(postDto.getContent())
                .likes(postDto.getLikes().stream().map(UserDto::getId).collect(Collectors.toSet()))
                .saved(postDto.getSaved().stream().map(UserDto::getId).collect(Collectors.toSet()))
                .comments(postDto.getComments().stream().map(commentMapper::toComment).collect(Collectors.toList()))
                .build();
    }

    public PostDto toPostDto(Post post) {
        return PostDto.builder()
                .title(post.getTitle())
                .userId(post.getUserId())
                .thumbnail(post.getThumbnail())
                .content(post.getContent())
                .likes(post.getLikes().stream().map(userApiClient::findUserById).collect(Collectors.toSet()))
                .saved(post.getSaved().stream().map(userApiClient::findUserById).collect(Collectors.toSet()))
                .comments(post.getComments().stream().map(commentMapper::toCommentDto).collect(Collectors.toList()))
                .userDto(userApiClient.findUserById(post.getUserId()))
                .build();
    }
}
