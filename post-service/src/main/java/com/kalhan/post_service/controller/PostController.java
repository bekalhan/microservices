package com.kalhan.post_service.controller;

import com.kalhan.post_service.dto.CreateUpdatePostRequest;
import com.kalhan.post_service.dto.PostDto;
import com.kalhan.post_service.dto.UserDto;
import com.kalhan.post_service.file.FileStorageService;
import com.kalhan.post_service.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final FileStorageService fileStorageService;

    @GetMapping("/get-all-posts")
    public ResponseEntity<List<PostDto>> getAllPosts() {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPosts());
    }

    @GetMapping("/get-post/{postId}")
    public ResponseEntity<PostDto> getPostById(
            @PathVariable(value = "postId") Integer id
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostById(id));
    }

    @GetMapping("/get-user-posts/{userId}")
    public ResponseEntity<List<PostDto>> getUserPosts(
            @PathVariable(value = "userId") String userId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getUserPosts(userId));
    }

    @GetMapping("/get-post-likes/{postId}")
    public ResponseEntity<List<UserDto>> getPostLikes(
            @PathVariable(value = "postId") Integer postId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostLikes(postId));
    }

    @GetMapping("/get-post-saved-count/{postId}")
    public ResponseEntity<Integer> getPostSavedCount(
            @PathVariable(value = "postId") Integer postId
    ){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getSavedCount(postId));
    }

    @GetMapping("/get-user-saved/{userId}")
    public ResponseEntity<List<PostDto>> getUserSaved(
            @PathVariable(value = "userId") String userId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getUserSaved(userId));
    }

    @PostMapping(value = "/create-post", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createPost(
            @RequestPart("file") @Valid MultipartFile thumbnailFile,
            @RequestParam @NotNull @Size(max = 30, message = "Title must be less than 30 characters") String title,
            @RequestParam @NotNull String content,
            @RequestHeader("id") String userId
    ) {
        String thumbnailPath = fileStorageService.saveFile(thumbnailFile); // Save the thumbnail
        if (thumbnailPath == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload thumbnail");
        }

        postService.createPost(thumbnailFile, title,content, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Post has been created");
    }

    @PutMapping("/update-post/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable(value = "postId") Integer postId,
            @RequestBody @Valid CreateUpdatePostRequest updatePostRequest,
            @RequestHeader("id") String userId
    ) {
        postService.updatePost(postId, updatePostRequest, userId);
        return ResponseEntity.status(HttpStatus.OK).body("Post has been updated");
    }

    @PatchMapping(value = "/upload-thumbnail/{postId}",consumes = {"multipart/form-data"})
    public ResponseEntity<?> uploadThumbnail(
            @PathVariable(value = "postId") Integer postId,
            @RequestPart("file") @Valid MultipartFile thumbnailFile,
            @RequestHeader("id") String userId
    ){
        postService.uploadThumbnail(postId,thumbnailFile,userId);
        return ResponseEntity.status(HttpStatus.OK).body("New thumbnail added to post");
    }

    @PatchMapping("/like-post/{postId}/{userId}")
    public ResponseEntity<?> likeAndUnlikePost(
            @PathVariable(value = "postId") Integer postId,
            @PathVariable(value = "userId") String userId
    ) {
        postService.likeAndUnlikePost(postId, userId);
        return ResponseEntity.status(HttpStatus.OK).body("You liked or unliked this post");
    }

    @PatchMapping("/save-post/{postId}/{userId}")
    public ResponseEntity<?> saveAndUnsavedPost(
            @PathVariable(value = "postId") Integer postId,
            @PathVariable(value = "userId") String userId
    ){
        postService.saveAndUnsavedPost(postId, userId);
        return ResponseEntity.status(HttpStatus.OK).body("You saved or unsaved this post");
    }

    @DeleteMapping("/delete-post/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable(value = "id") Integer id,
            @RequestHeader("id") String userId
    ) {
        postService.deletePost(id, userId);
        return ResponseEntity.status(HttpStatus.OK).body("Post has been deleted");
    }

}
