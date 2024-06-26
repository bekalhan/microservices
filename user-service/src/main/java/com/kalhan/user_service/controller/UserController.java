package com.kalhan.user_service.controller;

import com.kalhan.user_service.dto.*;
import com.kalhan.user_service.entity.User;
import com.kalhan.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create-new-user")
    public ResponseEntity<User> createNewUser(@RequestBody UserDto userCreateRequest){
        User user = userService.createNewUser(userCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/find-user")
    public ResponseEntity<User> findUserByEmail(@RequestParam String email){
        User user = userService.findUserByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @GetMapping("/find-user/{id}")
    public ResponseEntity<User> findUserById(@PathVariable(value = "id") String id){
        User user = userService.findUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PatchMapping("/activate-account/{id}")
    public ResponseEntity<?> activateUserAccount(@PathVariable(value = "id") String id){
        userService.activateAccount(id);
        return ResponseEntity.status(HttpStatus.OK).body("User account has been activated");
    }

    @PatchMapping("/change-password/{id}")
    public ResponseEntity<?> changePassword(
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest,
            @PathVariable(value = "id") String id,
            @RequestParam Boolean isReset
    ){
        userService.changePassword(changePasswordRequest,id,isReset);
        return ResponseEntity.status(HttpStatus.OK).body("Password has been changed successfully");
    }

    @PatchMapping("/block-user")
    public ResponseEntity<?> blockUserAccount(@RequestBody UserRequest blockUserRequest){
        userService.blockUserAccount(blockUserRequest);
        return ResponseEntity.status(HttpStatus.OK).body("User account has been block by admin");
    }

    @PatchMapping(value = "/upload-photo",consumes = "multipart/form-data")
    public ResponseEntity<?> uploadUserPhoto(
            @RequestPart("file") MultipartFile file,
            @RequestHeader("username") String username
    ){
        userService.uploadUserPhoto(file, username);
        return ResponseEntity.ok("Photo uploaded successfully");
    }

    @PatchMapping("/follow-user")
    public ResponseEntity<?> followUserAccount(
            @RequestBody UserFollowRequest userFollowRequest,
            @RequestHeader("username") String username
    ){
        userService.followUserAccount(userFollowRequest,username);
        return ResponseEntity.status(HttpStatus.OK).body("You has been followed this user successfully");
    }

    @PatchMapping("/unfollow-user")
    public ResponseEntity<?> unfollowUserAccount(
            @RequestBody UserFollowRequest userFollowRequest,
            @RequestHeader("username") String username
    ){
        userService.unfollowUserAccount(userFollowRequest,username);
        return ResponseEntity.status(HttpStatus.OK).body("You has been unfollowed this user successfully");
    }

    @GetMapping("/get-followers/{id}")
    public ResponseEntity<Set<FollowDto>> getUserFollowers(
            @PathVariable(value = "id") String id
    ){
       Set<FollowDto> userDtoSet = userService.getUserFollowers(id);
       return ResponseEntity.status(HttpStatus.OK).body(userDtoSet);
    }

    @GetMapping("/get-following/{id}")
    public ResponseEntity<Set<FollowDto>> getUserFollowing(
            @PathVariable(value = "id") String id
    ){
        Set<FollowDto> userDtoSet = userService.getUserFollowing(id);
        return ResponseEntity.status(HttpStatus.OK).body(userDtoSet);
    }

}
