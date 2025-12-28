package com.vti.springdatajpa.controller;

import com.vti.springdatajpa.dto.ContactDTO;
import com.vti.springdatajpa.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    @GetMapping("/frequent")
    public ResponseEntity<List<ContactDTO>> getFrequentContacts(@RequestParam(defaultValue = "5") int limit) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(contactService.getFrequentContacts(username));
    }
}
