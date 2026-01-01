package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.UserProfileDTO;
import com.vti.springdatajpa.dto.UserDto;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileDTO getProfile(String userName) {
        User user = findUserByUsernameOrEmail(userName);

        UserProfileDTO dto = new UserProfileDTO();
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setUserName(user.getUserName());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setMembership(user.getMembership());

        return dto;
    }

    /**
     * Flexible user lookup - tries username first, then email
     * This fixes JWT identity mismatch issues
     */
    private User findUserByUsernameOrEmail(String identity) {
        System.out.println("UserService - Looking for user with identity: " + identity);

        // Try username first
        User user = userRepository.findByUserName(identity).orElse(null);
        if (user != null) {
            System.out.println("UserService - Found user by username: " + identity);
            return user;
        }

        // Try email
        user = userRepository.findByEmail(identity).orElse(null);
        if (user != null) {
            System.out.println("UserService - Found user by email: " + identity);
            return user;
        }

        System.out.println("UserService - User not found with identity: " + identity);
        throw new RuntimeException("User not found with identity: " + identity);
    }
}
