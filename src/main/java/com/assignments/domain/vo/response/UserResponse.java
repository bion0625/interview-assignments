package com.assignments.domain.vo.response;

import com.assignments.domain.entity.User;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class UserResponse {
    private Long id;
    private String username;
    private String name;
    private String gender;
    private Integer age;
    private String phone;

    private List<PostResponse> posts;

    public UserResponse(Long id, String username, String name, String gender, Integer age, String phone) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
    }

    public UserResponse(Long id, String username, String name, String gender, Integer age, String phone, List<PostResponse> posts) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.phone = phone;
        this.posts = posts;
    }

    public static UserResponse of(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getGender(),
                user.getAge(),
                user.getPhone());
    }

    public static UserResponse ofWithPost(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getGender(),
                user.getAge(),
                user.getPhone(),
                user.getPosts() == null ? null : user.getPosts().stream()
                        .map(PostResponse::of).collect(Collectors.toList()));
    }
}
