package com.assignments.domain.vo.request;

import com.assignments.domain.entity.User;
import lombok.Data;

@Data
public class UserRequest {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String gender;
    private Integer age;
    private String phone;

    public User toEntity() {
        User entity = new User();
        entity.setId(this.id);
        entity.setUsername(this.username);
        entity.setPassword(this.password);
        entity.setName(this.name);
        entity.setGender(this.gender);
        entity.setAge(this.age);
        entity.setPhone(this.phone);
        return entity;
    }
}
