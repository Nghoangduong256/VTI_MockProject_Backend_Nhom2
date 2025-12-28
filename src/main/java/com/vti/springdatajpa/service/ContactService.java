package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.ContactDTO;
import com.vti.springdatajpa.entity.Contact;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.ContactRepository;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    public List<ContactDTO> getFrequentContacts(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Contact> contacts = contactRepository.findByUserId(user.getId(), PageRequest.of(0, 5));

        return contacts.stream().map(c -> {
            ContactDTO dto = new ContactDTO();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dto.setAvatarUrl(c.getAvatarUrl());
            return dto;
        }).collect(Collectors.toList());
    }
}
