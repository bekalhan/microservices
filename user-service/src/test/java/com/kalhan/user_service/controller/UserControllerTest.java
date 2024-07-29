package com.kalhan.user_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kalhan.user_service.dto.ChangePasswordRequest;
import com.kalhan.user_service.dto.UserDto;
import com.kalhan.user_service.dto.UserHelloDto;
import com.kalhan.user_service.entity.User;
import com.kalhan.user_service.service.UserService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.BDDMockito.willDoNothing;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserHelloDto userHelloDto;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void givenUserDto_whenCreateNewUser_thenReturnUser() throws Exception {
        //given
        UserDto userDto = new UserDto();
        userDto.setId("i1");
        userDto.setFirstname("berat");
        userDto.setLastname("kalhan");
        userDto.setEmail("berat@gmail.com");
        userDto.setRoles(List.of("ROLE_USER"));

        User user = new User();
        user.setId("i1");
        user.setFirstname("berat");
        user.setLastname("kalhan");
        user.setEmail("berat@gmail.com");
        user.setRoles(List.of("ROLE_USER"));

        BDDMockito.given(userService.createNewUser(ArgumentMatchers.any(UserDto.class)))
                .willReturn(user);

        //when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/user/create-new-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)));

        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(user.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstname", CoreMatchers.is(user.getFirstname())));
    }

    @Test
    public void givenEmail_whenFindUserByEmail_thenReturnUser() throws Exception{
        //given
        String email = "berat@gmail.com";

        User user = new User();
        user.setId("i1");
        user.setFirstname("berat");
        user.setLastname("kalhan");
        user.setEmail("berat@gmail.com");
        user.setRoles(List.of("ROLE_USER"));

        BDDMockito.given(userService.findUserByEmail(BDDMockito.anyString()))
                .willReturn(user);

        //when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/user/find-user")
                .param("email", "berat@gmail.com")
        );

        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(user.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstname", CoreMatchers.is(user.getFirstname())));
    }

    @Test
    public void givenId_whenFindUserById_thenReturnUser() throws Exception{
        //given
        String id = "i1";

        User user = new User();
        user.setId("i1");
        user.setFirstname("berat");
        user.setLastname("kalhan");
        user.setEmail("berat@gmail.com");
        user.setRoles(List.of("ROLE_USER"));

        BDDMockito.given(userService.findUserById(BDDMockito.anyString()))
                .willReturn(user);

        //when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.get("/user/find-user/{id}","i1")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(user.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstname", CoreMatchers.is(user.getFirstname())));
    }

    @Test
    public void givenId_whenActivateAccount_thenShouldReturnAnything() throws Exception{
        //given
        String id = "i1";

        User user = new User();
        user.setId("i1");
        user.setFirstname("berat");
        user.setLastname("kalhan");
        user.setEmail("berat@gmail.com");
        user.setRoles(List.of("ROLE_USER"));

        willDoNothing().given(userService).activateAccount(BDDMockito.anyString());

        //when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/user/activate-account/{id}","i1")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void givenId_whenChangePassword_thenShouldReturnAnything() throws Exception{
        //given
        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest("oldPass123", "newPass123","newPass123");

        String id = "i1";

        User user = new User();
        user.setId("i1");
        user.setFirstname("berat");
        user.setLastname("kalhan");
        user.setEmail("berat@gmail.com");
        user.setRoles(List.of("ROLE_USER"));

        willDoNothing().given(userService).changePassword(BDDMockito.any(ChangePasswordRequest.class), BDDMockito.anyString(), BDDMockito.anyBoolean());


        //when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/user/change-password/{id}","i1")
                .param("isReset", "false")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordRequest)));

        //then
        result.andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


}
