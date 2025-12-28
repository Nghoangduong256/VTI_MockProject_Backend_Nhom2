package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.service.impl.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final UserServiceImpl userService;

    @GetMapping("")
    public ResponseEntity<?> me() {
        return ResponseEntity.ok(userService.me());
    }

}
