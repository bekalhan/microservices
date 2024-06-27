package com.kalhan.post_service.service;

import com.kalhan.user_service.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "http://localhost:8082", value = "USER-SERVICE")
public interface UserApiClient {
    @RequestMapping(value = "/user/find-user", method = RequestMethod.GET)
    User findUserByEmail(@RequestParam("email") String email);

    @RequestMapping(value = "/user/find-user/{id}", method = RequestMethod.GET)
    User findUserById(@PathVariable("id") String id);
}
