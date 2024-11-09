package com.assignments.controller;

import com.assignments.config.SecurityConfig;
import com.assignments.domain.vo.response.PostResponse;
import com.assignments.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@DisplayName("단위 테스트: About [PostController]")
class PostControllerTest {

    private final MockMvc mockMvc;

    @MockBean
    private PostService postService;

    public PostControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void testGetPostSuccess() throws Exception {
        // given
        when(postService.get(any())).thenReturn(Optional.of(new PostResponse()));

        // when & then
        mockMvc.perform(get("/posts/" + 1)).andExpect(status().isOk());
    }

    @Test
    public void testAddPostFailure() throws Exception {
        // given
        when(postService.add(any(), any())).thenReturn(new PostResponse());

        // when & then
        mockMvc.perform(post("/posts")).andExpect(status().isForbidden());
    }

    @Test
    public void testUpdatePostFailure() throws Exception {
        // given
        when(postService.update(any(), any(), any())).thenReturn(Optional.of(new PostResponse()));

        // when & then
        mockMvc.perform(post("/posts/" + 1)).andExpect(status().isForbidden());
    }

    @Test
    public void testDeletePostFailure() throws Exception {
        // given
        when(postService.update(any(), any(), any())).thenReturn(Optional.of(new PostResponse()));

        // when & then
        mockMvc.perform(delete("/posts/" + 1)).andExpect(status().isForbidden());
    }

}