package com.assignments.domain.vo.response;

import com.assignments.domain.entity.Post;
import lombok.Getter;

@Getter
public class PostResponse {
    private Long id;
    private String title;
    private String content;

    private UserResponse user;

    public PostResponse() {}

    public PostResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public PostResponse(Long id, String title, String content, UserResponse user) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public static PostResponse of(Post post) {
        return new PostResponse(post.getId(), post.getTitle(), post.getContent());
    }

    public static PostResponse ofWithUser(Post post) {
        return new PostResponse(post.getId(), post.getTitle(), post.getContent(), UserResponse.of(post.getUser()));
    }
}
