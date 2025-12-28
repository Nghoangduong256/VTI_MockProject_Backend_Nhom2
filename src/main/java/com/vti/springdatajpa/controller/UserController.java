package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.UserProfileDTO;
import com.vti.springdatajpa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile() {
        // Assuming the principal is the User object as set in JwtAuthenticationFilter
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName;
        if (principal instanceof com.vti.springdatajpa.entity.User) {
            userName = ((com.vti.springdatajpa.entity.User) principal).getUserName();
        } else {
            userName = principal.toString();
        }
        return ResponseEntity.ok(userService.getProfile(userName));
    }
}
