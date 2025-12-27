package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
public class MeController {

    @Autowired
    private UserService userService;

    @GetMapping("")
    public ResponseEntity<?> me() {
        return ResponseEntity.ok(userService.me());
    }

}
