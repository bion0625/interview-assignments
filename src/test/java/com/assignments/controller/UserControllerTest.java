package com.assignments.controller;

import com.assignments.config.SecurityConfig;
import com.assignments.domain.entity.User;
import com.assignments.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class UserControllerTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public UserControllerTest(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testAddUserSuccess() throws Exception {
        // given
        when(userRepository.findByUsernameAndDeletedAtIsNull(any())).thenReturn(Optional.empty());
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
    public void testGetUserSuccess() throws Exception {
        // given
        User expectedUser = new User();
        expectedUser.setId(1L);
        when(userRepository.findById(any())).thenReturn(Optional.of(expectedUser));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + getToken());

        // when
        MvcResult result = mockMvc.perform(get("/users/" + expectedUser.getId())
                        .headers(headers))
                .andExpect(status().isOk()).andReturn();
        String content = result.getResponse().getContentAsString();
        User user = objectMapper.readValue(content, User.class);

        // then
        assert user.getId().equals(expectedUser.getId());
    }

    @Test
    public void testUpdateUserSuccess() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setName("modify");
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + getToken());
        Map<String, String> request = Map.of(
                "id", "1",
                "username", "test",
                "password", "qwer135!",
                "name", "testName",
                "gender", "M",
                "age", "30",
                "phone", "01012341234");

        // when & then
        MvcResult result = mockMvc.perform(put("/users/" + request.get("id"))
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        User modifiedUser = objectMapper.readValue(content, User.class);
        assert modifiedUser.getName().equals(request.get("name"));
    }

    private String getToken() {
        return Jwts.builder()
                .setSubject("username")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1시간 유효
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

}