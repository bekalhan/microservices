package com.kalhan.security_service.service;

import com.kalhan.security_service.dto.*;
import com.kalhan.security_service.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(url = "http://localhost:8082", value = "USER-SERVICE")
public interface UserApiClient {

    @RequestMapping(value = "/user/create-new-user", method = RequestMethod.POST)
    User createNewUser(@RequestBody UserCreateRequest userCreateRequest);

    @RequestMapping(value = "/user/find-user", method = RequestMethod.GET)
    User findUserByEmail(@RequestParam("email") String email);

    @RequestMapping(value = "/user/find-user/{id}", method = RequestMethod.GET)
    User findUserById(@PathVariable("id") String id);

    @RequestMapping(value = "/user/activate-account/{id}", method = RequestMethod.PATCH)
    void activateUserAccount(@PathVariable("id") String id);

    @RequestMapping(value = "/user/change-password/{id}", method = RequestMethod.PATCH)
    void changeUserPassword(@RequestBody PasswordResetRequest request, @PathVariable(value = "id") String id,@RequestParam boolean isReset);

}
