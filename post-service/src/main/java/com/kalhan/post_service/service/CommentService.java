package com.kalhan.post_service.service;

import com.kalhan.post_service.dto.CommentDto;
import com.kalhan.post_service.dto.UserDto;
import com.kalhan.post_service.entity.Comment;
import com.kalhan.post_service.entity.Post;
import com.kalhan.post_service.handler.exception.ResourceNoAccessException;
import com.kalhan.post_service.handler.exception.ResourceNotFoundException;
import com.kalhan.post_service.mapper.CommentMapper;
import com.kalhan.post_service.repository.CommentRepository;
import com.kalhan.post_service.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserApiClient userApiClient;

    public void createComment(Integer postId, CommentDto request,String userId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with this id " + postId));

        request.setUserId(userId);
        request.setLikes(new HashSet<>());
        Comment comment = commentMapper.toComment(request);
        comment.setPost(post);

        postRepository.save(post);
        commentRepository.save(comment);
    }

    public void updateComment(Integer commentId, CommentDto request,String userId){
       Comment comment = approveIdentityAndReturnComment(commentId,userId);
        comment.setContent(request.getContent());
        commentRepository.save(comment);
    }

    public void deleteComment(Integer commentId,String userId){
        Comment comment = approveIdentityAndReturnComment(commentId,userId);
        commentRepository.deleteById(commentId);
    }

    private Comment approveIdentityAndReturnComment(Integer commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with this id " + commentId));

        UserDto apiUser = userApiClient.findUserById(userId);

        if (!Objects.equals(comment.getUserId(), userId) && !apiUser.getRoles().contains("ROLE_ADMIN")) {
            throw new ResourceNoAccessException("You have no access to this resource");
        }

        return comment;
    }

}
