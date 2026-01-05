package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.UserAvatarUpdateDTO;
import com.vti.springdatajpa.dto.UserDto;
import com.vti.springdatajpa.dto.UserProfileDTO;
import com.vti.springdatajpa.dto.UserProfileUpdateDTO;
import com.vti.springdatajpa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDTO> getProfile() {
        String identity = getIdentityFromSecurityContext();
        return ResponseEntity.ok(userService.getProfile(identity));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody UserProfileUpdateDTO dto
    ) {
        userService.updateProfile(getIdentityFromSecurityContext(), dto);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @PutMapping("/profile/avatar")
    public ResponseEntity<?> updateAvatar(
            @RequestBody UserAvatarUpdateDTO dto
    ) {
        userService.updateAvatar(
                getIdentityFromSecurityContext(),
                dto.getAvatar()
        );
        return ResponseEntity.ok("Avatar updated successfully");
    }


    private String getIdentityFromSecurityContext() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof com.vti.springdatajpa.entity.User) {
            return ((com.vti.springdatajpa.entity.User) principal).getUserName();
        }

        if (principal instanceof String) {
            return (String) principal;
        }

        throw new RuntimeException(
                "Unsupported principal type: " + principal.getClass().getName()
        );
    }
}
