package com.kalhan.post_service.controller;

import com.kalhan.post_service.dto.CreateUpdatePostRequest;
import com.kalhan.post_service.dto.PostDto;
import com.kalhan.post_service.dto.UserDto;
import com.kalhan.post_service.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/get-all-posts")
    public ResponseEntity<List<PostDto>>  getAllPosts(){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPosts());
    }

    @GetMapping("/get-post/{postId}")
    public ResponseEntity<PostDto> getPostById(
            @PathVariable(value = "postId") Integer id
    ){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostById(id));
    }

    @GetMapping("/get-user-posts/{userId}")
    public ResponseEntity<List<PostDto>> getUserPosts(
            @PathVariable(value = "userId") String userId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getUserPosts(userId));
    }

    @GetMapping("/get-post-likes/{postId}")
    public ResponseEntity<List<UserDto>> getPostLikes(
            @PathVariable(value = "postId") Integer postId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostLikes(postId));
    }

    @PostMapping("/create-post")
    public ResponseEntity<?> createPost(
            @RequestBody @Valid CreateUpdatePostRequest createPostRequest,
            @RequestHeader("username") String username
    ){
        postService.createPost(createPostRequest,username);
        return ResponseEntity.status(HttpStatus.CREATED).body("Post has been created");
    }

    @PutMapping("/update-post/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable(value = "id") Integer id,
            @RequestBody @Valid CreateUpdatePostRequest updatePostRequest,
            @RequestHeader("username") String username
    ){
        postService.updatePost(id,updatePostRequest,username);
        return ResponseEntity.status(HttpStatus.OK).body("Post has been updated");
    }

    @PatchMapping("/like-post/{postId}/{userId}")
    public ResponseEntity<?> LikeAndUnlikePost(
            @PathVariable(value = "postId") Integer postId,
            @PathVariable(value = "userId") String userId
    ){
        postService.likeAndUnlikePost(postId,userId);
        return ResponseEntity.status(HttpStatus.OK).body("You like or unlike this post");
    }

    @DeleteMapping("/delete-post/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable(value = "id") Integer id,
            @RequestHeader("username") String username
    ){
        postService.deletePost(id,username);
        return ResponseEntity.status(HttpStatus.OK).body("Post has been deleted");
    }

}
