package com.kalhan.security_service.service;

import com.kalhan.security_service.dto.UserCreateRequest;
import com.kalhan.security_service.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "http://localhost:8082",value = "USER-SERVICE")
public interface UserApiClient {
    @PostMapping("/user/create-new-user")
    User createNewUser(@RequestBody UserCreateRequest userCreateRequest);

    @GetMapping("/user/find-user")
    User findUserByEmail(@RequestParam String email);
}
