package com.kalhan.security_service.controller;

import com.kalhan.security_service.dto.ChangePasswordRequest;
import com.kalhan.security_service.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("secure")
@RequiredArgsConstructor
public class SecureController {

    private final AuthenticationService service;

    @GetMapping("/heartbeat")
    public String sayHello(){
        return "Hello from secure endpoint!";
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            Principal connectedUser
    ) {
        service.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }
}
