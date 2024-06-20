package com.kalhan.user_service.mapper;

import com.kalhan.user_service.dto.UserDto;
import com.kalhan.user_service.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public User toUser(UserDto userDto){
        return User.builder()
                .firstname(userDto.getFirstname())
                .lastname(userDto.getLastname())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .accountLocked(userDto.isAccountLocked())
                .enabled(userDto.isEnabled())
                .roles(userDto.getRoles())
                .build();
    }

    public UserDto toUserDto(User user){
        return UserDto.builder()
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .password(user.getPassword())
                .isAccountLocked(user.isAccountLocked())
                .enabled(user.isEnabled())
                .roles(user.getRoles()).build();
    }
}
