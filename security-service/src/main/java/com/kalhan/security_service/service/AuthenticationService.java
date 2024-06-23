package com.kalhan.security_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.HttpHeaders;
import com.kalhan.security_service.dto.*;
import com.kalhan.security_service.entity.ActivationToken;
import com.kalhan.security_service.entity.Token;
import com.kalhan.security_service.entity.User;
import com.kalhan.security_service.handler.exception.*;
import com.kalhan.security_service.jwt.JwtService;
import com.kalhan.security_service.kafka.producer.KafkaProducer;
import com.kalhan.security_service.kafka.properties.UserRegisterTopicProperties;
import com.kalhan.security_service.repository.ActivationTokenRepository;
import com.kalhan.security_service.repository.RoleRepository;
import com.kalhan.security_service.repository.TokenRepository;
import com.kalhan.security_service.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.Principal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.kafka.support.KafkaHeaders.KEY;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserApiClient userApiClient;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final AuthenticationManager authenticationManager;
    private final KafkaProducer kafkaProducer;
    private final UserRegisterTopicProperties userRegisterTopicProperties;

    public AuthenticationResponse register(RegistrationRequest request) {
        var userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));

        User apiUser = userApiClient.findUserByEmail(request.getEmail());

        if(apiUser != null){
            throw new UserAlreadyExistException("User already exist with this email : "+apiUser.getEmail());
        }

        // Create user dto and send to user service for register user
        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isAccountLocked(false)
                .enabled(false)
                .roles(List.of(userRole.getName()))  // Use role name
                .build();

        User user = userApiClient.createNewUser(userCreateRequest);

        // Create JWT and refresh token
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        // Save tokens
        saveUserToken(user, jwtToken);

        //send mail using kafka
        sendMailUsingKafka(userCreateRequest, user);

        // Return tokens
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {


        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User apiUser = userApiClient.findUserByEmail(request.getEmail());

        if(!apiUser.isEnabled()){
            throw new InactiveAccountException("Account is not active please check your email and active your account");
        }

        var claims = new HashMap<String, Object>();
        var user = ((User) auth.getPrincipal());
        claims.put("fullName", user.getFullName());

        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();

    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = userApiClient.findUserByEmail(userEmail);
            if(user == null){
                throw new UsernameNotFoundException("User not found");
            }
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public void activateAccount(String token) throws MessagingException {
        ActivationToken savedToken = activationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Token is invalid"));

        User apiUser = userApiClient.findUserById(savedToken.getUserId());
        if (apiUser == null) {
            throw new UsernameNotFoundException("User not found");
        }

        UserCreateRequest userCreateRequest = UserCreateRequest.builder()
                .firstname(apiUser.getFirstname())
                .lastname(apiUser.getLastname())
                .email(apiUser.getEmail())
                .password(passwordEncoder.encode(apiUser.getPassword()))
                .isAccountLocked(false)
                .enabled(false)
                .build();

        if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
            sendMailUsingKafka(userCreateRequest,apiUser);
            throw new TokenExpiredException("Activation token has expired and a new token has been sent");
        }

        //change enable true for user from user-service
        userApiClient.activateUserAccount(apiUser.getId());
        savedToken.setValidatedAt(LocalDateTime.now());
        savedToken.setUser(apiUser);
        activationTokenRepository.save(savedToken);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findByValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) return;

        validUserTokens.forEach(t -> {
            t.setExpired(true);
            t.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .userId(user.getId())
                .token(jwtToken)
                .revoked(false)
                .expired(false)
                .build();
        tokenRepository.save(token);
    }

    private void sendMailUsingKafka(UserCreateRequest userCreateRequest, User user) {
        var newToken = generateAndSaveActivationToken(user);
        userCreateRequest.setToken(newToken);
        Map<String, Object> headers = new HashMap<>();
        headers.put(TOPIC, userRegisterTopicProperties.getTopicName());
        headers.put(KEY, user.getId());

        kafkaProducer.sendMessage(new GenericMessage<>(userCreateRequest, headers));
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = ActivationToken.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .userId(user.getId())
                .build();
        activationTokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
        String characters = "123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }
}
