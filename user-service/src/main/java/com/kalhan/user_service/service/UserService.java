package com.kalhan.user_service.service;

import com.kalhan.user_service.dto.UserRequest;
import com.kalhan.user_service.dto.ChangePasswordRequest;
import com.kalhan.user_service.dto.UserDto;
import com.kalhan.user_service.entity.User;
import com.kalhan.user_service.file.FileStorageService;
import com.kalhan.user_service.handler.exception.IncorrectCredentialsException;
import com.kalhan.user_service.mapper.UserMapper;
import com.kalhan.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public User createNewUser(UserDto request){
        User user = userMapper.toUser(request);
        return userRepository.save(user);
    }

    public User findUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User findUserById(String id){return userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));}

    public void activateAccount(String id){
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void changePassword(ChangePasswordRequest request,String id,boolean isReset){
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // check if the current password is correct
        if(!isReset){
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword()) && !isReset) {
                        throw new IncorrectCredentialsException("Wrong password");
            }
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IncorrectCredentialsException("Passwords does not match");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public void blockUserAccount(UserRequest request){
        User user = userRepository.findById(request.id()).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setAccountLocked(true);
        userRepository.save(user);
    }

    public void uploadUserPhoto(MultipartFile file, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        String profilePicture = fileStorageService.saveFile(file, user.getId());
        user.setProfilePhoto(profilePicture);
        userRepository.save(user);
    }

}
