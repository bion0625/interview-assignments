package com.assignments.service;

import com.assignments.domain.entity.User;
import com.assignments.domain.vo.request.UserRequest;
import com.assignments.domain.vo.response.UserResponse;
import com.assignments.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(@Autowired UserRepository userRepository, @Autowired PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("USER") // 권한 설정
                .build();
    }

    public List<UserResponse> getAll() {
        return userRepository.findAllByDeletedAtIsNull().stream()
                .map(UserResponse::of)
                .collect(Collectors.toList());
    }

    public boolean isDuplicateByUsername(String username) {
        return userRepository.findByUsernameAndDeletedAtIsNull(username).isPresent();
    }

    @Transactional
    public UserResponse add(UserRequest user) {
        User entity = user.toEntity();
        entity.setPassword(passwordEncoder.encode(user.getPassword()));
        return UserResponse.of(userRepository.save(entity));
    }

    public Optional<UserResponse> get(Long id) {
        return userRepository.findByIdAndDeletedAtIsNull(id).map(UserResponse::ofWithPost);
    }

    @Transactional
    public Optional<UserResponse> update(Long id, UserRequest updatedUser) {
        return userRepository.findByIdAndDeletedAtIsNull(id)
                .map(user -> {
                    user.setName(updatedUser.getName());
                    user.setGender(updatedUser.getGender());
                    user.setAge(updatedUser.getAge());
                    user.setPhone(updatedUser.getPhone());
                    user.setUpdatedAt(LocalDateTime.now());
                    return UserResponse.of(user);
                });
    }

    @Transactional
    public void delete(Long id) {
        userRepository.findByIdAndDeletedAtIsNull(id)
                .ifPresent(user -> user.setDeletedAt(LocalDateTime.now()));
    }
}
