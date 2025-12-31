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
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("User Profile - JWT identity: " + principal);
        System.out.println("Identity type: " + principal.getClass().getName());
        
        String userName;
        if (principal instanceof com.vti.springdatajpa.entity.User) {
            userName = ((com.vti.springdatajpa.entity.User) principal).getUserName();
            System.out.println("Extracted username from User object: " + userName);
        } else if (principal instanceof String) {
            userName = (String) principal;
            System.out.println("Using string identity: " + userName);
        } else {
            throw new RuntimeException("Unsupported identity type: " + principal.getClass().getName());
        }
        
        return ResponseEntity.ok(userService.getProfile(userName));
    }
}
