package com.assignments.controller;

import com.assignments.config.SecurityConfig;
import com.assignments.domain.entity.User;
import com.assignments.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@DisplayName("단위 테스트: About [AuthController]")
class AuthControllerTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public AuthControllerTest(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void testLoginSuccess() throws Exception {
        // given
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(userRepository.findByUsernameAndDeletedAtIsNull(any())).thenReturn(Optional.of(new User()));

        Map<String, String> request = Map.of(
                "username", "test",
                "password", "qwer135!");

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void testLoginFailure() throws Exception {
        // given
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        when(userRepository.findByUsernameAndDeletedAtIsNull(any())).thenReturn(Optional.of(new User()));

        Map<String, String> request = Map.of(
                "username", "test",
                "password", "qwer135!");

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testProtectedSuccess() throws Exception {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + getToken());

        // when & then
        mockMvc.perform(get("/protected")
                        .headers(headers))
                .andExpect(status().isOk());
    }

    private String getToken() {
        return Jwts.builder()
                .setSubject("username")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1시간 유효
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    @Test
    public void testProtectedFailure() throws Exception {
        // given
        // when & then
        mockMvc.perform(get("/protected"))
                .andExpect(status().isForbidden());
    }
}