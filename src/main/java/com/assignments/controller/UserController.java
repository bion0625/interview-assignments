package com.assignments.controller;

import com.assignments.domain.vo.request.UserRequest;
import com.assignments.domain.vo.response.UserResponse;
import com.assignments.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(userService.getAll());
    }

    @PostMapping
    public ResponseEntity<UserResponse> addUser(@RequestBody UserRequest request) {
        if (userService.isDuplicateByUsername(request.getUsername())) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.add(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return userService.get(id)
                .filter(user -> getAuthenticationName().filter(name -> name.equals(user.getUsername())).isPresent())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        if (userService.get(id).filter(user -> getAuthenticationName().filter(name -> name.equals(user.getUsername())).isPresent()).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return userService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.get(id).filter(user -> getAuthenticationName().filter(name -> name.equals(user.getUsername())).isPresent()).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}
