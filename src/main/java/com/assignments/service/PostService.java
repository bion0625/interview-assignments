package com.assignments.service;

import com.assignments.domain.entity.Post;
import com.assignments.domain.vo.response.PostResponse;
import com.assignments.repository.PostRepository;
import com.assignments.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public PostResponse add(Post post, String username) {
        post.setUser(userRepository.findByUsernameAndDeletedAtIsNull(username).orElseThrow());
        post.setCreatedAt(LocalDateTime.now());
        return PostResponse.of(postRepository.save(post));
    }

    public Optional<PostResponse> get(Long id) {
        return postRepository.findByIdAndDeletedAtIsNull(id).map(PostResponse::of);
    }

    @Transactional
    public Optional<PostResponse> update(Long id, Post updatedPost, String username) {
        return postRepository.findByIdAndDeletedAtIsNull(id)
                .filter(post -> username.equals(post.getUser().getUsername()))
                .map(post -> {
                    post.setTitle(updatedPost.getTitle());
                    post.setContent(updatedPost.getContent());
                    post.setUpdatedAt(LocalDateTime.now());
                    return PostResponse.ofWithUser(post);
                });
    }

    @Transactional
    public void delete(Long id, String username) {
        postRepository.findByIdAndDeletedAtIsNull(id)
                .filter(post -> username.equals(post.getUser().getUsername()))
                .ifPresent(post -> post.setDeletedAt(LocalDateTime.now()));
    }
}
