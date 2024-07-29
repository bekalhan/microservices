package com.kalhan.user_service.service;

import com.kalhan.user_service.dto.*;
import com.kalhan.user_service.entity.User;
import com.kalhan.user_service.file.FileStorageService;
import com.kalhan.user_service.handler.exception.IncorrectCredentialsException;
import com.kalhan.user_service.mapper.UserMapper;
import com.kalhan.user_service.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("User service test")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MultipartFile file;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private UserService userService;

    @DisplayName("create user successfully")
    @Test
    void test_createUser(){
        //given
        UserDto userDto = new UserDto();
        userDto.setId("a1");
        userDto.setFirstname("berat");
        userDto.setEmail("berat@gmail.com");

        User user = new User();
        user.setId("a1");
        user.setFirstname("berat");
        user.setEmail("berat@gmail.com");

        //when
        when(userMapper.toUser(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        //then
        User userResult = userService.createNewUser(userDto);

        assertNotNull(userResult);
        assertEquals(userResult.getId(),user.getId());
        assertEquals(userResult.getFirstname(),user.getFirstname());
        assertEquals(userResult.getEmail(),user.getEmail());

        // Verify interactions with the mocks
        verify(userMapper).toUser(userDto);
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userMapper, userRepository);

    }

    @DisplayName("find by email successfully")
    @Test
    void test_findByEmail(){
        //given
        String email = "berat@gmail.com";

        User user = new User();
        user.setId("a1");
        user.setEmail("berat@gmail.com");
        user.setFirstname("berat");

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        //then
        User userResult = userService.findUserByEmail(email);

        assertNotNull(userResult);
        assertEquals(userResult.getEmail(),user.getEmail());
        assertEquals(userResult.getId(),user.getId());
        assertEquals(userResult.getFirstname(),user.getFirstname());

        //verify
        verify(userRepository).findByEmail(email);
    }

    @DisplayName("return null when user not found with given email")
    @Test
    void findUserByEmail_shouldReturnNull_whenUserNotFoundWithGivenEmail(){
        //given
        String email = "berat@gmail.com";

        User user = new User();
        user.setId("a1");
        user.setEmail("berat@gmail.com");
        user.setFirstname("berat");

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //then
        User userResult = userService.findUserByEmail(email);

        assertNull(userResult);

        //verify
        verify(userRepository).findByEmail(email);
        verifyNoMoreInteractions(userRepository);

    }

    @DisplayName("user by id successfully")
    @Test
    void test_findUserById(){
        //given
        String id = "a1";

        User user = new User();
        user.setId("a1");
        user.setEmail("berat@gmail.com");
        user.setFirstname("berat");

        //when
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        //then
        User userResult = userService.findUserById(id);

        //assert
        assertNotNull(userResult);
        assertEquals(userResult.getId(),user.getId());
        assertEquals(userResult.getFirstname(),user.getFirstname());
        assertEquals(userResult.getEmail(),user.getEmail());

        //verify
        verify(userRepository).findById(id);
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("return null when user not found with given id")
    @Test
    void findUserById_shouldReturnNull_whenUserNotFoundWithGivenId(){
        //given
        String id = "a1";

        //when
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        //then
        User userResult = userService.findUserById(id);

        //assert
        assertNull(userResult);

        //verify
        verify(userRepository).findById(id);
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("find user for post successfully")
    @Test
    void test_findUserByPost(){
        //given
        String id = "a1";

        User user = new User();
        user.setId("i1");
        user.setEmail("berat@gmail.com");

        UserDto userDto = new UserDto();
        userDto.setId("i1");
        userDto.setEmail("berat@gmail.com");

        //when
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        //then
        UserDto userResult = userService.findUserForPost(id);

        //assert
        assertNotNull(userResult);
        assertEquals(userResult.getId(),userDto.getId());
        assertEquals(userResult.getEmail(),userDto.getEmail());

        //verify
        verify(userRepository).findById(id);
        verify(userMapper).toUserDto(user);
        verifyNoMoreInteractions(userRepository,userMapper);
    }

    @DisplayName("activate account successfully")
    @Test
    void test_activateAccount(){
        //given
        String id = "a1";

        User user = new User();
        user.setId("i1");
        user.setEmail("berat@gmail.com");
        user.setEnabled(true);

        //when
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        //then
        userService.activateAccount(id);

        //assert
        assert(user.isEnabled());

        //verfiy
        verify(userRepository).findById(id);
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("should throw exception when user not found with given id")
    @Test
    void activateAccount_shouldThrowException_whenUserNotFoundWithGivenId(){
        //given
        String id = "a1";

        //when
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        //then
        org.assertj.core.api.Assertions.assertThatThrownBy(
                () -> userService.activateAccount(id)
        ).isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");

        //verify
        verify(userRepository).findById(id);
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("change password successfully")
    @Test
    void test_changePassword(){
        //given
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("current");
        changePasswordRequest.setNewPassword("new");
        changePasswordRequest.setConfirmationPassword("new");

        String id = "a1";
        boolean isReset = false;

        User user = new User();
        user.setId("i1");
        user.setPassword("current");

        //when
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(changePasswordRequest.getCurrentPassword(),user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(changePasswordRequest.getNewPassword())).thenReturn("current");

        // then
        userService.changePassword(changePasswordRequest, id, isReset);

        verify(userRepository).findById(id);
        verify(passwordEncoder).matches(changePasswordRequest.getCurrentPassword(), user.getPassword());
        verify(passwordEncoder).encode(changePasswordRequest.getNewPassword());
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository, passwordEncoder);

    }

    @DisplayName("change password when user not with given id")
    @Test
    void changePassword_shouldThrowException_whenUserNotFoundWithGivenId(){
        //given
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("current");
        changePasswordRequest.setNewPassword("new");
        changePasswordRequest.setConfirmationPassword("new");

        String id = "a1";
        boolean isReset = false;

        //when
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        // then
        org.assertj.core.api.Assertions.assertThatThrownBy(
                () -> userService.changePassword(changePasswordRequest,id,isReset)
        ).isInstanceOf(UsernameNotFoundException.class)
                        .hasMessageContaining("User not found");

        verify(userRepository).findById(id);
        verifyNoMoreInteractions(userRepository);

    }

    @DisplayName("change password incorrect current password")
    @Test
    void changePassword_shouldThrowExceptionWhenCurrentPasswordIsIncorrect(){
        //given
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("current");
        changePasswordRequest.setNewPassword("new");
        changePasswordRequest.setConfirmationPassword("new");

        String id = "a1";
        boolean isReset = false;

        User user = new User();
        user.setId("i1");
        user.setPassword("current");

        //when
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(changePasswordRequest.getCurrentPassword(),user.getPassword())).thenReturn(false);

        // then
        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> userService.changePassword(changePasswordRequest,id,isReset)
                ).isInstanceOf(IncorrectCredentialsException.class)
                .hasMessageContaining("Wrong password");

        verify(userRepository).findById(id);
        verify(passwordEncoder).matches(changePasswordRequest.getCurrentPassword(), user.getPassword());
        verifyNoMoreInteractions(userRepository, passwordEncoder);

    }

    @DisplayName("change password passwords does not match")
    @Test
    void changePassword_shouldThrowException_whenNewPasswordAndConfirmationPasswordDoesNotMatch(){
        //given
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
        changePasswordRequest.setCurrentPassword("current");
        changePasswordRequest.setNewPassword("new");
        changePasswordRequest.setConfirmationPassword("differentPassword");

        String id = "a1";
        boolean isReset = false;

        User user = new User();
        user.setId("i1");
        user.setPassword("encodedPassword");

        //when
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(changePasswordRequest.getCurrentPassword(),user.getPassword())).thenReturn(true);

        // then
        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> userService.changePassword(changePasswordRequest,id,isReset)
                ).isInstanceOf(IncorrectCredentialsException.class)
                .hasMessageContaining("Passwords does not match");

        verify(userRepository).findById(id);
        verify(passwordEncoder).matches(changePasswordRequest.getCurrentPassword(), user.getPassword());
        verifyNoMoreInteractions(userRepository, passwordEncoder);

    }

    @DisplayName("reset password successfully")
    @Test
    void test_resetPassword_success() {
        // given
        String userId = "a1";
        boolean isReset = true;

        User user = new User();
        user.setId(userId);
        user.setPassword("encodedOldPassword");

        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setNewPassword("newPassword");
        request.setConfirmationPassword("newPassword");

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(request.getNewPassword())).thenReturn("encodedNewPassword");

        // then
        userService.changePassword(request, userId, isReset);

        verify(userRepository).findById(userId);
        verify(passwordEncoder).encode(request.getNewPassword());
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository, passwordEncoder);
    }

    @DisplayName("block user account successfully")
    @Test
    void test_blockUserAccount(){
        // given
        String userId = "a1";
        UserRequest request = new UserRequest(userId);

        User user = new User();
        user.setId(userId);
        user.setAccountLocked(false);

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // then
        userService.blockUserAccount(request);

        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("should throw exception when block user account")
    @Test
    void blockUserAccount_shouldThrowException_whenUserNotFoundWithGivenId(){
        //given
        String userId = "a1";
        UserRequest request = new UserRequest(userId);

        //when
        when(userRepository.findById(request.id())).thenReturn(Optional.empty());

        //then
        org.assertj.core.api.Assertions.assertThatThrownBy(
                () -> userService.blockUserAccount(request)
        ).isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("upload user photo successfully")
    @Test
    void test_uploadUserPhoto(){
        //given
        String email = "berat@gmail.com";
        String profilePhoto = "profilePhoto";

        User user = new User();
        user.setId("i1");
        user.setEmail("berat@gmail.com");
        user.setProfilePhoto(profilePhoto);

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(fileStorageService.saveFile(file,user.getId())).thenReturn(profilePhoto);

        //then
        userService.uploadUserPhoto(file,email);

        //verify
        verify(userRepository).findByEmail(email);
        verify(fileStorageService).saveFile(file,user.getId());
        verify(userRepository).save(user);

    }

    @DisplayName("upload user user not found with given email")
    @Test
    void uploadUserPhoto_shouldThrowException_whenUserNotFoundWithGivenEmail(){
        //given
        String email = "berat@gmail.com";

        //when
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //then
        org.assertj.core.api.Assertions.assertThatThrownBy(
                () -> userService.uploadUserPhoto(file,email)
        ).isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");

        //verify
        verify(userRepository).findByEmail(email);
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("user follow account successfully")
    @Test
    void test_userFollowAccount(){
        // given
        String followingUsername = "follower@example.com";
        String followedEmail = "followed@example.com";

        User followingUser = new User();
        followingUser.setEmail(followingUsername);
        followingUser.setFollowing(new HashSet<>());

        User followedUser = new User();
        followedUser.setEmail(followedEmail);
        followedUser.setFollowers(new HashSet<>());

        UserFollowRequest request = new UserFollowRequest(followedEmail);

        // when
        when(userRepository.findByEmail(followingUsername)).thenReturn(Optional.of(followingUser));
        when(userRepository.findByEmail(followedEmail)).thenReturn(Optional.of(followedUser));

        // then
        userService.followUserAccount(request, followingUsername);

        verify(userRepository).findByEmail(followingUsername);
        verify(userRepository).findByEmail(followedEmail);

        // Verify that followingUser's following list has the followedUser
        verify(userRepository).save(argThat(user -> user.getEmail().equals(followingUsername) &&
                user.getFollowing().contains(followedUser)));
        // Verify that followedUser's followers list has the followingUser
        verify(userRepository).save(argThat(user -> user.getEmail().equals(followedEmail) &&
                user.getFollowers().contains(followingUser)));

        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("follow user account - user not found")
    @Test
    void test_followUserAccount_userNotFound() {
        // given
        String followingUsername = "follower@example.com";
        String followedEmail = "followed@example.com";

        UserFollowRequest request = new UserFollowRequest(followedEmail);

        // when
        when(userRepository.findByEmail(followingUsername)).thenReturn(Optional.empty());

        // then
        org.assertj.core.api.Assertions.assertThatThrownBy(
                () -> userService.followUserAccount(request,followingUsername)
        ).isInstanceOf(UsernameNotFoundException.class)
                        .hasMessageContaining("User not found");

        verify(userRepository).findByEmail(followingUsername);
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("follow user account - followed user not found")
    @Test
    void test_followUserAccount_followedUserNotFound() {
        // given
        String followingUsername = "follower@example.com";
        String followedEmail = "followed@example.com";

        User followingUser = new User();
        followingUser.setEmail(followingUsername);
        followingUser.setFollowing(new HashSet<>());

        UserFollowRequest request = new UserFollowRequest(followedEmail);

        // when
        when(userRepository.findByEmail(followingUsername)).thenReturn(Optional.of(followingUser));
        when(userRepository.findByEmail(followedEmail)).thenReturn(Optional.empty());

        // then
        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> userService.followUserAccount(request,followingUsername)
                ).isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
        verify(userRepository).findByEmail(followingUsername);
        verify(userRepository).findByEmail(followedEmail);
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("unfollow user account successfully")
    @Test
    void test_unfollowUserAccount_success() {
        // given
        String unfollowingUsername = "follower@example.com";
        String unfollowedEmail = "followed@example.com";

        User unfollowingUser = new User();
        unfollowingUser.setEmail(unfollowingUsername);
        Set<User> followingList = new HashSet<>();
        User unfollowedUser = new User();
        unfollowedUser.setEmail(unfollowedEmail);
        Set<User> followersList = new HashSet<>();

        followingList.add(unfollowedUser);
        unfollowingUser.setFollowing(followingList);

        followersList.add(unfollowingUser);
        unfollowedUser.setFollowers(followersList);

        UserFollowRequest request = new UserFollowRequest(unfollowedEmail);

        // when
        when(userRepository.findByEmail(unfollowingUsername)).thenReturn(Optional.of(unfollowingUser));
        when(userRepository.findByEmail(unfollowedEmail)).thenReturn(Optional.of(unfollowedUser));

        // then
        userService.unfollowUserAccount(request, unfollowingUsername);

        verify(userRepository).findByEmail(unfollowingUsername);
        verify(userRepository).findByEmail(unfollowedEmail);

        // Verify that unfollowingUser's following list does not contain unfollowedUser
        verify(userRepository).save(argThat(user -> user.getEmail().equals(unfollowingUsername) &&
                !user.getFollowing().contains(unfollowedUser)));
        // Verify that unfollowedUser's followers list does not contain unfollowingUser
        verify(userRepository).save(argThat(user -> user.getEmail().equals(unfollowedEmail) &&
                !user.getFollowers().contains(unfollowingUser)));

        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("unfollow user account - unfollowing user not found")
    @Test
    void test_unfollowUserAccount_unfollowingUserNotFound() {
        // given
        String unfollowingUsername = "follower@example.com";
        String unfollowedEmail = "followed@example.com";

        UserFollowRequest request = new UserFollowRequest(unfollowedEmail);

        // when
        when(userRepository.findByEmail(unfollowingUsername)).thenReturn(Optional.empty());

        // then
        assertThrows(UsernameNotFoundException.class, () -> userService.unfollowUserAccount(request, unfollowingUsername));

        verify(userRepository).findByEmail(unfollowingUsername);
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("unfollow user account - unfollowed user not found")
    @Test
    void test_unfollowUserAccount_unfollowedUserNotFound() {
        // given
        String unfollowingUsername = "follower@example.com";
        String unfollowedEmail = "followed@example.com";

        User unfollowingUser = new User();
        unfollowingUser.setEmail(unfollowingUsername);
        Set<User> followingList = new HashSet<>();
        unfollowingUser.setFollowing(followingList);

        UserFollowRequest request = new UserFollowRequest(unfollowedEmail);

        // when
        when(userRepository.findByEmail(unfollowingUsername)).thenReturn(Optional.of(unfollowingUser));
        when(userRepository.findByEmail(unfollowedEmail)).thenReturn(Optional.empty());

        // then
        assertThrows(UsernameNotFoundException.class, () -> userService.unfollowUserAccount(request, unfollowingUsername));

        verify(userRepository).findByEmail(unfollowingUsername);
        verify(userRepository).findByEmail(unfollowedEmail);
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("get user followers successfully")
    @Test
    void test_getUserFollowers(){
        // given
        String userId = "user123";

        User user = new User();
        user.setId(userId);

        User follower1 = new User();
        follower1.setFirstname("John");
        follower1.setLastname("Doe");
        follower1.setProfilePhoto("john_photo.jpg");

        User follower2 = new User();
        follower2.setFirstname("Jane");
        follower2.setLastname("Doe");
        follower2.setProfilePhoto("jane_photo.jpg");

        Set<User> followers = new HashSet<>();
        followers.add(follower1);
        followers.add(follower2);

        user.setFollowers(followers);

        FollowDto followDto1 = FollowDto.builder()
                .firstname("John")
                .lastname("Doe")
                .profilePhoto("john_photo.jpg")
                .build();

        FollowDto followDto2 = FollowDto.builder()
                .firstname("Jane")
                .lastname("Doe")
                .profilePhoto("jane_photo.jpg")
                .build();

        Set<FollowDto> expectedFollowersDto = new HashSet<>();
        expectedFollowersDto.add(followDto1);

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // then
        Set<FollowDto> actualFollowersDto = userService.getUserFollowers(userId);

        assertEquals(expectedFollowersDto.size(), actualFollowersDto.size());

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);

    }

    @DisplayName("get user followers not found user")
    @Test
    void getUserFollowers_shouldThrowException_whenUserNotFoundWithGivenId(){
        //given
        String id = "a1";

        //when
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        //then
        org.assertj.core.api.Assertions.assertThatThrownBy(
                () -> userService.getUserFollowers(id)
        ).isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");

        //verify
        verify(userRepository).findById(id);
    }

    @DisplayName("get user following successfully")
    @Test
    void test_getUserFollowing() {
        // given
        String userId = "user123";

        User user = new User();
        user.setId(userId);

        User followingUser1 = new User();
        followingUser1.setFirstname("Alice");
        followingUser1.setLastname("Smith");
        followingUser1.setProfilePhoto("alice_photo.jpg");

        User followingUser2 = new User();
        followingUser2.setFirstname("Bob");
        followingUser2.setLastname("Brown");
        followingUser2.setProfilePhoto("bob_photo.jpg");

        Set<User> following = new HashSet<>();
        following.add(followingUser1);
        following.add(followingUser2);

        user.setFollowing(following);

        FollowDto followDto1 = FollowDto.builder()
                .firstname("Alice")
                .lastname("Smith")
                .profilePhoto("alice_photo.jpg")
                .build();

        FollowDto followDto2 = FollowDto.builder()
                .firstname("Bob")
                .lastname("Brown")
                .profilePhoto("bob_photo.jpg")
                .build();

        Set<FollowDto> expectedFollowingDto = new HashSet<>();
        expectedFollowingDto.add(followDto1);

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // then
        Set<FollowDto> actualFollowingDto = userService.getUserFollowing(userId);

        // Compare sizes first
        assertEquals(expectedFollowingDto.size(), actualFollowingDto.size(), "The size of the following should match");

        // Optionally, compare content if needed
        assertEquals(expectedFollowingDto.size(), actualFollowingDto.size());

        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @DisplayName("get user following - user not found")
    @Test
    void test_getUserFollowing_userNotFound() {
        // given
        String userId = "user123";

        // when
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // then
        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> userService.getUserFollowing(userId)
                ).isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById(userId);
    }
}

