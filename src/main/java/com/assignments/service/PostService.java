package com.assignments.service;

import com.assignments.domain.entity.Post;
import com.assignments.domain.vo.request.PostRequest;
import com.assignments.domain.vo.response.PostResponse;
import com.assignments.repository.PostRepository;
import com.assignments.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    public List<PostResponse> getAll() {
        return postRepository.findAllByDeletedAtIsNull().stream()
                .map(PostResponse::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public PostResponse add(PostRequest post, String username) {
        Post entity = post.toEntity();
        entity.setUser(userRepository.findByUsernameAndDeletedAtIsNull(username).orElseThrow());
        entity.setCreatedAt(LocalDateTime.now());
        return PostResponse.of(postRepository.save(entity));
    }

    public Optional<PostResponse> get(Long id) {
        return postRepository.findByIdAndDeletedAtIsNull(id).map(PostResponse::of);
    }

    @Transactional
    public Optional<PostResponse> update(Long id, PostRequest updatedPost, String username) {
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
