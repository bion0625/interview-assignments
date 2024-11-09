package com.assignments.service;

import com.assignments.domain.vo.request.PostRequest;
import com.assignments.domain.vo.request.UserRequest;
import com.assignments.domain.vo.response.PostResponse;
import com.assignments.domain.vo.response.UserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("통합 테스트 시나리오: About [PostService]")
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    static UserResponse user1;
    static UserResponse user2;

    static PostRequest post;

    @BeforeEach
    public void setUp() {
        UserRequest user = new UserRequest();
        user.setPassword("qwer135!");

        user.setId(1L);
        user.setUsername("username");
        user.setName("name");
        user1 = userService.add(user);
        user.setId(2L);
        user.setUsername("username2");
        user.setName("name2");
        user2 = userService.add(user);
        post = new PostRequest();
        post.setId(1L);
        post.setTitle("title");
        post.setContent("content");
    }

    @Test
    @DisplayName("시나리오: 등록 후 등록한 본인이 수정 - SUCCESS")
    public void givenAdd_whenPut_thenSuccess() {
        // given
        PostResponse postResponse = postService.add(post, user1.getUsername());
        PostRequest updatedPost = new PostRequest();
        updatedPost.setId(postResponse.getId());
        updatedPost.setTitle(postResponse.getTitle() + " modify");
        updatedPost.setContent(postResponse.getContent() + " modify");

        // when
        postService.update(postResponse.getId(), updatedPost, user1.getUsername());

        // then
        PostResponse resultPost = postService.get(postResponse.getId()).get();
        Assertions.assertNotEquals(resultPost.getTitle(), postResponse.getTitle());
        Assertions.assertNotEquals(resultPost.getContent(), postResponse.getContent());
        Assertions.assertEquals(resultPost.getTitle(), updatedPost.getTitle());
        Assertions.assertEquals(resultPost.getContent(), updatedPost.getContent());
    }

    @Test
    @DisplayName("시나리오: 타인의 게시물을 수정 - FAILURE")
    public void givenAdd_whenPut_thenFailure() {
        // given
        PostResponse postResponse = postService.add(post, user1.getUsername());
        PostRequest updatedPost = new PostRequest();
        updatedPost.setId(postResponse.getId());
        updatedPost.setTitle(postResponse.getTitle() + " modify");
        updatedPost.setContent(postResponse.getContent() + " modify");

        // when
        postService.update(postResponse.getId(), updatedPost, user2.getUsername());

        // then
        PostResponse resultPost = postService.get(postResponse.getId()).get();
        Assertions.assertEquals(resultPost.getTitle(), postResponse.getTitle());
        Assertions.assertEquals(resultPost.getContent(), postResponse.getContent());
        Assertions.assertNotEquals(resultPost.getTitle(), updatedPost.getTitle());
        Assertions.assertNotEquals(resultPost.getContent(), updatedPost.getContent());
    }

    @Test
    @DisplayName("시나리오: 등록 후 등록한 본인이 삭제 - SUCCESS")
    public void givenAdd_whenDelete_thenSuccess() {
        // given
        PostResponse postResponse = postService.add(post, user1.getUsername());

        // when
        postService.delete(postResponse.getId(), user1.getUsername());

        // then
        Assertions.assertTrue(postService.get(postResponse.getId()).isEmpty());
    }

    @Test
    @DisplayName("시나리오: 타인의 게시물을 삭제 - FAILURE")
    public void givenAdd_whenDelete_thenFailure() {
        // given
        PostResponse postResponse = postService.add(post, user1.getUsername());

        // when
        postService.delete(postResponse.getId(), user2.getUsername());

        // then
        Assertions.assertTrue(postService.get(postResponse.getId()).isPresent());
    }
}