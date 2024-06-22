package com.kalhan.security_service.controller;

import com.kalhan.security_service.dto.AuthenticationRequest;
import com.kalhan.security_service.dto.AuthenticationResponse;
import com.kalhan.security_service.dto.ChangePasswordRequest;
import com.kalhan.security_service.dto.RegistrationRequest;
import com.kalhan.security_service.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @Valid RegistrationRequest request
    )  {
        AuthenticationResponse authenticationResponse = service.register(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(authenticationResponse);
    }

    @PostMapping("/authenticate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        AuthenticationResponse authenticationResponse = service.authenticate(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(authenticationResponse);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request,response);
    }

    @GetMapping("/activate-account")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        service.activateAccount(token);
    }


}