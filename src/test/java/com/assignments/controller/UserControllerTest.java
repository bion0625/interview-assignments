package com.assignments.controller;

import com.assignments.domain.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("통합 테스트 시나리오: About [UserController]")
class UserControllerTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    public UserControllerTest(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    static final Map<String, String> request = Map.of(
            "username", "test",
            "password", "qwer135!",
            "name", "testName",
            "gender", "M",
            "age", "30",
            "phone", "01012341234");

    static final Map<String, String> requestByUpdateName = Map.of(
            "username", "test",
            "password", "qwer135!",
            "name", "modify",
            "gender", "M",
            "age", "30",
            "phone", "01012341234");

    static final Map<String, String> request2 = Map.of(
            "username", "test2",
            "password", "qwer135!",
            "name", "testName",
            "gender", "M",
            "age", "30",
            "phone", "01012341234");

    @Test
    @DisplayName("시나리오: 가입 후 로그인 한 토큰으로 자기 자신의 정보 보기 - SUCCESS")
    public void givenAdd_whenGet_thenSuccess() throws Exception {
        // given
        User user = addUserByRequestAndReturnUser();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + loginByRequestAndReturnToken());

        // when & then
        mockMvc.perform(get("/users/" + user.getId()).headers(headers))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("시나리오: 가입 후 로그인 한 토큰으로 타인의 정보 보기 - FAILURE")
    public void givenAdd_whenGet_thenFailure() throws Exception {
        // given
        // 가입1
        User otherUser = addUserByRequestAndReturnUser();
        // 가입2 유저의 토큰 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + addUserByRequest2AndReturnUserToken());

        // when & then
        // 가입2 유저의 토큰으로 가입1 유저의 정보 접근
        mockMvc.perform(get("/users/" + otherUser.getId()).headers(headers))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("시나리오: 가입 후 로그인 한 토큰으로 자기 자신의 정보 수정 - SUCCESS")
    public void givenAdd_whenPut_thenSuccess() throws Exception {
        // given
        User user = addUserByRequestAndReturnUser();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + loginByRequestAndReturnToken());

        // when
        mockMvc.perform(put("/users/" + user.getId())
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestByUpdateName)))
                .andExpect(status().isOk());

        // then
        MvcResult result = mockMvc.perform(get("/users/" + user.getId())
                        .headers(headers))
                .andExpect(status().isOk()).andReturn();
        User newUser = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);

        Assertions.assertEquals(newUser.getId(), user.getId());
        Assertions.assertEquals(newUser.getName(), requestByUpdateName.get("name"));
    }

    @Test
    @DisplayName("시나리오: 가입 후 로그인 한 토큰으로 타인의 정보 수정 - FAILURE")
    public void givenAdd_whenPut_thenFailure() throws Exception {
        // given
        // 가입1
        User otherUser = addUserByRequestAndReturnUser();
        // 가입2 유저의 토큰 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + addUserByRequest2AndReturnUserToken());

        // when & then
        mockMvc.perform(put("/users/" + otherUser.getId())
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestByUpdateName)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("시나리오: 가입 후 로그인 한 토큰으로 탈퇴했을 때 다시 로그인 실패 - FAILURE")
    public void givenDelete_whenLogin_thenFailure() throws Exception {
        // given
        User user = addUserByRequestAndReturnUser();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + loginByRequestAndReturnToken());

        // when
        // 탈퇴
        mockMvc.perform(delete("/users/"+user.getId()).headers(headers)).andExpect(status().isOk());

        // then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    private User addUserByRequestAndReturnUser() throws Exception {
        MvcResult result = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andReturn();
        return objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
    }

    private String loginByRequestAndReturnToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()).andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    private String addUserByRequest2AndReturnUserToken() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk()).andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }
}