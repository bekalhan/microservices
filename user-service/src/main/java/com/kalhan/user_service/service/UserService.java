package com.kalhan.user_service.service;

import com.kalhan.user_service.dto.ChangePasswordRequest;
import com.kalhan.user_service.dto.UserDto;
import com.kalhan.user_service.entity.User;
import com.kalhan.user_service.mapper.UserMapper;
import com.kalhan.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        return userRepository.findByEmail(email);
    }

    public void activateAccount(String id){
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void changePassword(ChangePasswordRequest request){
        User user = userRepository.findById(request.getId()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
    }
}
