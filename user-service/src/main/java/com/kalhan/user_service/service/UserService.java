package com.kalhan.user_service.service;

import com.kalhan.user_service.dto.UserDto;
import com.kalhan.user_service.entity.User;
import com.kalhan.user_service.mapper.UserMapper;
import com.kalhan.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User createNewUser(UserDto request){
        User user = userMapper.toUser(request);
        return userRepository.save(user);
    }

    public User findUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow();
    }
}
