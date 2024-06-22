package com.kalhan.user_service.controller;

import com.kalhan.user_service.dto.ChangePasswordRequest;
import com.kalhan.user_service.dto.FindUserRequest;
import com.kalhan.user_service.dto.UserDto;
import com.kalhan.user_service.entity.User;
import com.kalhan.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
        userService.changePassword(changePasswordRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Password has been changed successfully");

    }
}
