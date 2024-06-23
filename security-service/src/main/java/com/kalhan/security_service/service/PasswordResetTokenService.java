package com.kalhan.security_service.service;

import com.kalhan.security_service.dto.PasswordResetRequest;
import com.kalhan.security_service.entity.PasswordResetToken;
import com.kalhan.security_service.entity.User;
import com.kalhan.security_service.handler.exception.IncorrectCredentialsException;
import com.kalhan.security_service.handler.exception.InvalidTokenException;
import com.kalhan.security_service.handler.exception.TokenExpiredException;
import com.kalhan.security_service.kafka.producer.KafkaProducer;
import com.kalhan.security_service.kafka.properties.UserPasswordResetProperties;
import com.kalhan.security_service.repository.PasswordResetTokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.kafka.support.KafkaHeaders.KEY;
import static org.springframework.kafka.support.KafkaHeaders.TOPIC;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserApiClient userApiClient;
    private final PasswordEncoder passwordEncoder;
    private final UserPasswordResetProperties userPasswordResetProperties;
    private final KafkaProducer kafkaProducer;

    public String resetPasswordRequest(PasswordResetRequest request, HttpServletRequest servletRequest){
        User user = userApiClient.findUserByEmail(request.getEmail());
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }
        String passwordResetToken = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(user,passwordResetToken);
        return passwordResetEmailLink(request,user,applicationUrl(servletRequest), passwordResetToken);
    }

    public void resetPassword(String token,PasswordResetRequest request){
        Boolean resetToken = validatePasswordResetToken(token);

        if(!resetToken){
            throw new InvalidTokenException("Token is invalid");
        }

        User apiUser = userApiClient.findUserByEmail(request.getEmail());

        if(apiUser == null){
            throw new UsernameNotFoundException("User not found");
        }

        changePassword(apiUser,request);

    }

    private void createPasswordResetTokenForUser(User user, String passwordToken) {
        PasswordResetToken passwordRestToken =
                PasswordResetToken.builder()
                        .user(user)
                        .userId(user.getId())
                        .token(passwordToken)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now().plusMinutes(15))
                        .build();
        passwordResetTokenRepository.save(passwordRestToken);
    }

    private String passwordResetEmailLink(PasswordResetRequest request,User user,String applicationUrl,String passwordToken){
        var url = applicationUrl+"/auth/reset-password?token="+passwordToken;
        sendMailUsingKafka(request,user,url);
        return url;
    }

    private void changePassword(User user,PasswordResetRequest request){
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IncorrectCredentialsException("Passwords does not match");
        }

        userApiClient.changeUserPassword(request,user.getId(),true);
    }

    public Boolean validatePasswordResetToken(String passwordResetToken) {
        PasswordResetToken passwordToken = passwordResetTokenRepository.findByToken(passwordResetToken);
        if(passwordToken == null){
            throw new InvalidTokenException("Token is invalid");
        }

        if (LocalDateTime.now().isAfter(passwordToken.getExpiresAt())) {
            //sendMailUsingKafka(userCreateRequest,apiUser);
            throw new TokenExpiredException("Password token has expired and a new token has been sent");
        }
        return true;
    }

    private void sendMailUsingKafka(PasswordResetRequest passwordResetRequest, User user,String url) {
        passwordResetRequest.setFirstname(user.getFirstname());
        passwordResetRequest.setLastname(user.getLastname());
        passwordResetRequest.setEmail(user.getEmail());
        passwordResetRequest.setToken(url);
        Map<String, Object> headers = new HashMap<>();
        headers.put(TOPIC, userPasswordResetProperties.getTopicName());
        headers.put(KEY, user.getId());

        kafkaProducer.sendMessage(new GenericMessage<>(passwordResetRequest, headers));
    }

    public String applicationUrl(HttpServletRequest request) {
        return "http://"+request.getServerName()+":"
                +request.getServerPort()+request.getContextPath();
    }
}
