package com.assignments.domain.vo.response;

import com.assignments.domain.entity.Post;
import lombok.Getter;

@Getter
public class PostResponse {
    private Long id;
    private String title;
    private String content;

    private UserResponse user;

    public PostResponse(Long id, String title, String content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    public static PostResponse of(Post post) {
        return new PostResponse(post.getId(), post.getTitle(), post.getContent());
    }
}
