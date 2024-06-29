package com.kalhan.post_service.service;

import com.kalhan.post_service.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "http://localhost:8082", value = "USER-SERVICE")
public interface UserApiClient {
   @RequestMapping(value = "/user/find-user-for-post/{id}", method = RequestMethod.GET)
    UserDto findUserById(@PathVariable("id") String id);
}
