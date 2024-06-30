package com.kalhan.post_service.controller;

import com.kalhan.post_service.dto.CommentDto;
import com.kalhan.post_service.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/create-comment/{postId}")
    public ResponseEntity<?> createComment(
            @PathVariable(value = "postId") Integer postId,
            @RequestBody CommentDto commentDto,
            @RequestHeader("id") String userId
            ){
        commentService.createComment(postId,commentDto,userId);
        return ResponseEntity.status(HttpStatus.OK).body("Comment has been added");
    }

    @PutMapping("/update-comment/{commentId}")
    public ResponseEntity<?> updateComment(
            @PathVariable(value = "commentId") Integer commentId,
            @RequestBody CommentDto commentDto,
            @RequestHeader("id") String userId
    ){
        commentService.updateComment(commentId,commentDto,userId);
        return ResponseEntity.status(HttpStatus.OK).body("Comment has been edited");
    }

    @DeleteMapping("/delete-comment/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable(value = "commentId") Integer commentId,
            @RequestHeader("id") String userId
    ){
        commentService.deleteComment(commentId,userId);
        return ResponseEntity.status(HttpStatus.OK).body("Comment has been deleted");
    }


}
