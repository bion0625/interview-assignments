package com.assignments.controller;

import com.assignments.domain.entity.User;
import com.assignments.domain.vo.response.UserResponse;
import com.assignments.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<UserResponse> addUser(@RequestBody User user) {
        Optional<User> optionalUser = userRepository.findByUsernameAndDeletedAtIsNull(user.getUsername());
        if (optionalUser.isPresent()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.of(userRepository.save(user)));
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return userRepository.findByIdAndDeletedAtIsNull(id)
                .filter(user -> getAuthenticationName().filter(name -> name.equals(user.getUsername())).isPresent())
                .map(UserResponse::ofWithPost)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userRepository.findByIdAndDeletedAtIsNull(id)
                .filter(user -> getAuthenticationName().filter(name -> name.equals(user.getUsername())).isPresent())
                .map(user -> {
                    user.setName(updatedUser.getName());
                    user.setGender(updatedUser.getGender());
                    user.setAge(updatedUser.getAge());
                    user.setPhone(updatedUser.getPhone());
                    user.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(UserResponse.of(user));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.findByIdAndDeletedAtIsNull(id)
                .filter(user -> getAuthenticationName().filter(name -> name.equals(user.getUsername())).isPresent())
                .ifPresent(user -> user.setDeletedAt(LocalDateTime.now()));
//        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
