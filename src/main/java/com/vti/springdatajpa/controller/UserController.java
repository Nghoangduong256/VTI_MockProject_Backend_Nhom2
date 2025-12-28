package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.UserDto;
import com.vti.springdatajpa.dto.UserProfileDTO;
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

    //Get by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileById(@PathVariable(name = "id") Integer id) {
        System.out.println("ID from request = " + id);
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    //Update profile
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable(name = "id") Integer id, @RequestBody UserDto userDto) {
        userService.updateUser(id, userDto);
        return new ResponseEntity<>("Update user success", HttpStatus.OK);
    }

    //Delete profile
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable(name = "id") Integer id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>("Delete user success", HttpStatus.OK);
    }

    //Update avatar
    @PutMapping("/{id}/avatar")
    public ResponseEntity<?> updateAvatar(@PathVariable(name = "id") Integer id, @RequestParam String avatarUrl) {
        userService.updateUserAvatar(id, avatarUrl);
        return new ResponseEntity<>("Update avatar success", HttpStatus.OK);
    }
}
