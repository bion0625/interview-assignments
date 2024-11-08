package com.assignments.controller;

import com.assignments.domain.entity.User;
import com.assignments.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<User> addUser(@RequestBody User user) {
        Optional<User> optionalUser = userRepository.findByUsernameAndDeletedAtIsNull(user.getUsername());
        if (optionalUser.isPresent()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .filter(user -> getAuthenticationName().filter(name -> name.equals(user.getUsername())).isPresent())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        return userRepository.findById(id)
                .filter(user -> getAuthenticationName().filter(name -> name.equals(user.getUsername())).isPresent())
                .map(user -> {
                    user.setName(updatedUser.getName());
                    user.setGender(updatedUser.getGender());
                    user.setAge(updatedUser.getAge());
                    user.setPhone(updatedUser.getPhone());
                    user.setUpdatedAt(LocalDateTime.now());
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.findById(id)
                .filter(user -> getAuthenticationName().filter(name -> name.equals(user.getUsername())).isPresent())
                .ifPresent(user -> user.setDeletedAt(LocalDateTime.now()));
//        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
