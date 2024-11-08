package com.assignments.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class BaseController {
    public Optional<String> getAuthenticationName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) return Optional.empty();
        return Optional.ofNullable(authentication.getName());
    }
}
