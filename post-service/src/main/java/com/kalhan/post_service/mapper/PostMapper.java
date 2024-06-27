package com.kalhan.post_service.mapper;

import com.kalhan.post_service.dto.PostDto;
import com.kalhan.post_service.entity.Post;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class PostMapper {

    public Post toPost(PostDto postDto) {
        return Post.builder()
                .title(postDto.getTitle())
                .userId(postDto.getUserId())
                .thumbnail(postDto.getThumbnail())
                .content(postDto.getContent())
                .likes(postDto.getLikes())
                .saved(postDto.getSaved())
                .comments(postDto.getComments())
                .build();
    }

    public PostDto toPostDto(Post post) {
        return PostDto.builder()
                .title(post.getTitle())
                .userId(post.getUserId())
                .thumbnail(post.getThumbnail())
                .content(post.getContent())
                .likes(post.getLikes())
                .saved(post.getSaved())
                .comments(post.getComments())
                .build();
    }
}
