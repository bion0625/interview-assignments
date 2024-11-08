package com.assignments.controller;

import com.assignments.domain.entity.User;
import com.assignments.domain.vo.request.AuthRequest;
import com.assignments.domain.vo.response.AuthResponse;
import com.assignments.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
public class AuthController {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        User user = userRepository.findByUsernameAndDeletedAtIsNull(request.getUsername())
                .orElse(null);

        if (user != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String token = Jwts.builder()
                    .setSubject(request.getUsername())
                    .setExpiration(new Date(System.currentTimeMillis() + 1800000))  // 30분
                    .signWith(SignatureAlgorithm.HS512, jwtSecret)
                    .compact();
            return ResponseEntity.ok(new AuthResponse(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/protected")
    public ResponseEntity<?> protectedRoute(@RequestHeader("Authorization") String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token.replace("Bearer ", ""));
            return ResponseEntity.ok("Access granted");
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
