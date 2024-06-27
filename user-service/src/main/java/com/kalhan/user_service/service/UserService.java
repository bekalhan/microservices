package com.kalhan.user_service.service;

import com.kalhan.user_service.dto.*;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findUserById(String id){return userRepository.findById(id).orElse(null);}

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

    public void followUserAccount(UserFollowRequest request,String username){
        User followingUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        User followedUser = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        //for following user
        Set<User> followingList = followingUser.getFollowing();
        followingList.add(followedUser);
        followingUser.setFollowing(followingList);
        userRepository.save(followingUser);

        //for followed user
        Set<User> followList = followedUser.getFollowers();
        followList.add(followingUser);
        followedUser.setFollowers(followList);
        userRepository.save(followedUser);
    }

    public void unfollowUserAccount(UserFollowRequest request,String username){
        User unfollowingUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        User unfollowedUser = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        //for unfollowing user
        Set<User> followinglist = unfollowingUser.getFollowing();
        followinglist.remove(unfollowedUser);
        unfollowingUser.setFollowing(followinglist);
        userRepository.save(unfollowingUser);

        //for unfollowed user
        Set<User> followList = unfollowedUser.getFollowers();
        followList.remove(unfollowingUser);
        unfollowedUser.setFollowers(followList);
        userRepository.save(unfollowedUser);
    }

    public Set<FollowDto> getUserFollowers(String id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        Set<FollowDto> followersDto = user.getFollowers().stream()
                .map(follower -> FollowDto.builder()
                        .firstname(follower.getFirstname())
                        .lastname(follower.getLastname())
                        .profilePhoto(follower.getProfilePhoto())
                        .build())
                .collect(Collectors.toSet());

        return followersDto;
    }

    public Set<FollowDto> getUserFollowing(String id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<FollowDto> followingDto = user.getFollowing().stream()
                .map(follower -> FollowDto.builder()
                        .firstname(follower.getFirstname())
                        .lastname(follower.getLastname())
                        .profilePhoto(follower.getProfilePhoto())
                        .build())
                .collect(Collectors.toSet());

        return followingDto;

    }

}
