package com.assignments.controller;

import com.assignments.domain.entity.Post;
import com.assignments.domain.entity.User;
import com.assignments.repository.PostRepository;
import com.assignments.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/posts")
public class PostController extends BaseController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Post> addPost(@RequestBody Post post) {
        User user = getAuthenticationName().map(name -> userRepository.findByUsernameAndDeletedAtIsNull(name)).orElseThrow().get();
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        Post savedPost = postRepository.save(post);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPost);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        return postRepository.findByIdAndDeletedAtIsNull(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post updatedPost) {
        return postRepository.findByIdAndDeletedAtIsNull(id)
                .filter(post -> getAuthenticationName().filter(name -> name.equals(post.getUser().getUsername())).isPresent())
                .map(post -> {
                    post.setTitle(updatedPost.getTitle());
                    post.setContent(updatedPost.getContent());
                    post.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(post);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postRepository.findByIdAndDeletedAtIsNull(id)
                .filter(post -> getAuthenticationName().filter(name -> name.equals(post.getUser().getUsername())).isPresent())
                .ifPresent(post -> post.setDeletedAt(LocalDateTime.now()));
//        postRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
