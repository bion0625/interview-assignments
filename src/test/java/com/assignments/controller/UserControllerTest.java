package com.assignments.controller;

import com.assignments.config.SecurityConfig;
import com.assignments.domain.entity.User;
import com.assignments.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class UserControllerTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    public UserControllerTest(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testGetUsersFailure() throws Exception {
        // given

        // when & then
        mockMvc.perform(get("/users")).andExpect(status().isForbidden());
    }

    @Test
    public void testAddUserSuccess() throws Exception {
        // given
        when(userRepository.findByUsernameAndDeletedAtIsNull(any())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(new User());
        Map<String, String> request = Map.of(
                "id", "100",
                "username", "test",
                "password", "qwer135!",
                "name", "testName",
                "gender", "M",
                "age", "30",
                "phone", "01012341234");

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testAddUserFailure() throws Exception {
        // given
        when(userRepository.findByUsernameAndDeletedAtIsNull(any())).thenReturn(Optional.of(new User()));
        Map<String, String> request = Map.of(
                "username", "test",
                "password", "qwer135!",
                "name", "testName",
                "gender", "M",
                "age", "30",
                "phone", "01012341234");

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetUserFailure() throws Exception {
        // given

        // when & then
        mockMvc.perform(get("/users/" + 1)).andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateUserFailure() throws Exception {
        // given

        // when & then
        mockMvc.perform(put("/users/" + 1)).andExpect(status().isForbidden());
    }

}