package com.assignments.controller;

import com.assignments.config.SecurityConfig;
import com.assignments.domain.entity.Post;
import com.assignments.repository.PostRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class PostControllerTest {

    private final MockMvc mockMvc;

    @MockBean
    private PostRepository postRepository;

    public PostControllerTest(@Autowired MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    public void testGetPostSuccess() throws Exception {
        // given
        when(postRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(new Post()));

        // when & then
        mockMvc.perform(get("/posts/" + 1)).andExpect(status().isOk());
    }

    @Test
    public void testAddPostFailure() throws Exception {
        // given
        when(postRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(new Post()));

        // when & then
        mockMvc.perform(post("/posts")).andExpect(status().isForbidden());
    }

    @Test
    public void testUpdatePostFailure() throws Exception {
        // given
        when(postRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(new Post()));

        // when & then
        mockMvc.perform(post("/posts/" + 1)).andExpect(status().isForbidden());
    }

    @Test
    public void testDeletePostFailure() throws Exception {
        // given
        when(postRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(new Post()));

        // when & then
        mockMvc.perform(delete("/posts/" + 1)).andExpect(status().isForbidden());
    }

}