package com.assignments.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity(name = "TB_USER")
public class User extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "gender")
    private String gender;
    @Column(name = "age")
    private Integer age;
    @Column(name = "phone")
    private String phone;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;
}
