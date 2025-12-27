package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.UserProfileDTO;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileDTO getProfile(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileDTO dto = new UserProfileDTO();
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setUserName(user.getUserName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setMembership(user.getMembership());

        return dto;
    }
}
