package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.UserDto;
import com.vti.springdatajpa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@CrossOrigin("*")
@RestController
@RequestMapping(value = "api/v1/profiles")
public class UserController {

    @Autowired
    private UserService userService;

    //Get by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileById(@PathVariable(name = "id") UUID id) {
        System.out.println("ID from request = " + id);
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }

    //Update profile
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable(name = "id") UUID id, @RequestBody UserDto userDto) {
        userService.updateUser(id, userDto);
        return new ResponseEntity<>("Update user success", HttpStatus.OK);
    }

    //Delete profile
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProfile(@PathVariable(name = "id") UUID id) {
        userService.deleteUserById(id);
        return new ResponseEntity<>("Delete user success", HttpStatus.OK);
    }
}
