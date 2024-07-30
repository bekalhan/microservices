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
import org.assertj.core.api.Assertions;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("User service test")
@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserApiClient userApiClient;
    @InjectMocks
    private CommentService commentService;

    @Test
    void test_createComment(){
        //given
        Integer postId = 1;
        String userId = "i1";

        Post post = new Post();
        post.setUserId(userId);
        post.setContent("post");

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setContent("content");
        comment.setLikes(new HashSet<>());

        CommentDto commentDto = new CommentDto();
        commentDto.setUserId(userId);
        commentDto.setContent("content");
        commentDto.setLikes(new HashSet<>());

        //when
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentMapper.toComment(any(CommentDto.class))).thenReturn(comment);

        //then
        commentService.createComment(postId,commentDto,userId);

        verify(postRepository, times(1)).findById(postId);
        verify(commentMapper, times(1)).toComment(any(CommentDto.class));
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_shouldThrowException_whenPostNotFoundWithGivenId(){
        //given
        Integer postId = 1;
        String userId = "i1";

        Post post = new Post();
        post.setUserId(userId);
        post.setContent("post");

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setContent("content");
        comment.setLikes(new HashSet<>());

        CommentDto commentDto = new CommentDto();
        commentDto.setUserId(userId);
        commentDto.setContent("content");
        commentDto.setLikes(new HashSet<>());

        //when
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        //then
        Assertions.assertThatThrownBy(
                () -> commentService.createComment(postId,commentDto,userId)
        ).isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post not found with this id ");

    }

    @Test
    void test_updateComment() {
        // given
        Integer commentId = 1;
        String userId = "i1";
        String newContent = "updated content";

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setContent("original content");

        CommentDto commentDto = new CommentDto();
        commentDto.setContent(newContent);

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setRoles(List.of("ROLE_USER"));

        // when
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userApiClient.findUserById(userId)).thenReturn(userDto);

        // then
        commentService.updateComment(commentId, commentDto, userId);

        verify(commentRepository, times(1)).findById(commentId);
        verify(userApiClient, times(1)).findUserById(userId);
        verify(commentRepository, times(1)).save(comment);
        assertEquals(newContent, comment.getContent());
    }

    @Test
    void test_updateComment_noAccess() {
        // given
        Integer commentId = 1;
        String userId = "i1";
        String anotherUserId = "i2";
        String newContent = "updated content";

        Comment comment = new Comment();
        comment.setUserId(anotherUserId); // Different user
        comment.setContent("original content");

        CommentDto commentDto = new CommentDto();
        commentDto.setContent(newContent);

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setRoles(List.of("ROLE_USER"));

        // when
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userApiClient.findUserById(userId)).thenReturn(userDto);

        // then
        assertThrows(ResourceNoAccessException.class, () -> {
            commentService.updateComment(commentId, commentDto, userId);
        });

        verify(commentRepository, times(1)).findById(commentId);
        verify(userApiClient, times(1)).findUserById(userId);
        verify(commentRepository, times(0)).save(any(Comment.class));
    }

    @Test
    void test_updateComment_notFound() {
        // given
        Integer commentId = 1;
        String userId = "i1";
        String newContent = "updated content";

        CommentDto commentDto = new CommentDto();
        commentDto.setContent(newContent);

        // when
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.updateComment(commentId, commentDto, userId);
        });

        verify(commentRepository, times(1)).findById(commentId);
        verify(userApiClient, times(0)).findUserById(anyString());
        verify(commentRepository, times(0)).save(any(Comment.class));
    }

    @Test
    void test_deleteComment(){
        //given
        Integer commentId = 1;
        String userId = "i1";

        Comment comment = new Comment();
        comment.setContent("comment content");

        UserDto apiUser = new UserDto();
        apiUser.setId("i1");
        apiUser.setRoles(List.of("ROLE_ADMIN"));

        //when
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userApiClient.findUserById(userId)).thenReturn(apiUser);

        //then
        commentService.deleteComment(commentId,userId);
    }

    @Test
    void test_deleteComment_noAccess() {
        // given
        Integer commentId = 1;
        String userId = "i1";
        String anotherUserId = "i2";
        String newContent = "updated content";

        Comment comment = new Comment();
        comment.setUserId(anotherUserId); // Different user
        comment.setContent("original content");

        UserDto userDto = new UserDto();
        userDto.setId(userId);
        userDto.setRoles(List.of("ROLE_USER"));

        // when
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(userApiClient.findUserById(userId)).thenReturn(userDto);

        // then
        assertThrows(ResourceNoAccessException.class, () -> {
            commentService.deleteComment(commentId, userId);
        });

        verify(commentRepository, times(1)).findById(commentId);
        verify(userApiClient, times(1)).findUserById(userId);
        verify(commentRepository, times(0)).save(any(Comment.class));
    }

    @Test
    void test_deleteComment_notFound() {
        // given
        Integer commentId = 1;
        String userId = "i1";

        // when
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // then
        assertThrows(ResourceNotFoundException.class, () -> {
            commentService.deleteComment(commentId, userId);
        });

        verify(commentRepository, times(1)).findById(commentId);
        verify(userApiClient, times(0)).findUserById(anyString());
        verify(commentRepository, times(0)).save(any(Comment.class));
    }

}
