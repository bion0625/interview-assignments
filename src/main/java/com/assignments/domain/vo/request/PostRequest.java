package com.assignments.domain.vo.request;

import com.assignments.domain.entity.Post;
import lombok.Data;

@Data
public class PostRequest {
    private Long id;
    private String title;
    private String content;

    public Post toEntity() {
        Post entity = new Post();
        entity.setId(this.id);
        entity.setTitle(this.title);
        entity.setContent(this.content);
        return entity;
    }
}
