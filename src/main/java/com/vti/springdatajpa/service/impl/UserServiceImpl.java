package com.vti.springdatajpa.service.impl;

import com.vti.springdatajpa.dto.UserDto;
import com.vti.springdatajpa.dto.UserOutputDTO;
import com.vti.springdatajpa.dto.UserProfileDTO;
import com.vti.springdatajpa.dto.UserProfileUpdateDTO;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.UserRepository;
import com.vti.springdatajpa.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.vti.springdatajpa.service.ImageHandlerService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserProfileDTO getProfile(String username) {
        User user = findUserByUsernameOrEmail(username);
        UserProfileDTO dto = new UserProfileDTO();

        // Identity
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());

        // Full name → First / Last
        if (user.getFullName() != null) {
            String[] parts = user.getFullName().split(" ", 2);
            dto.setFirstName(parts[0]);
            dto.setLastName(parts.length > 1 ? parts[1] : "");
        }

        // Avatar (Base64 + fallback URL)
        dto.setAvatar(
                user.getAvatar() != null
                        ? "data:image/png;base64," + user.getAvatar()
                        : null
        );

        dto.setAvatarUrl(user.getAvatarUrl());

        // Status
        dto.setVerified(user.isVerified());
        dto.setMembership(user.getMembership());

        // Contact & personal info
        dto.setPhone(user.getPhone());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setAddress(user.getAddress());

        return dto;
    }

    @Override
    public void updateProfile(String identity, UserProfileUpdateDTO dto) {
        User user = findUserByUsernameOrEmail(identity);

        // Update fullName từ first + last
        if (dto.getFirstName() != null || dto.getLastName() != null) {
            String firstName = dto.getFirstName() != null ? dto.getFirstName() : "";
            String lastName = dto.getLastName() != null ? dto.getLastName() : "";
            user.setFullName((firstName + " " + lastName).trim());
        }

        user.setPhone(dto.getPhone());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAddress(dto.getAddress());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    @Override
    public void updateAvatar(String identity, String avatarBase64) {
        User user = findUserByUsernameOrEmail(identity);

        if (avatarBase64 == null || avatarBase64.isBlank()) {
            throw new IllegalArgumentException("Avatar data is empty");
        }

        // 🚨 CHẶN TRÙNG PREFIX
        if (avatarBase64.startsWith("data:image")) {
            avatarBase64 = avatarBase64.substring(
                    avatarBase64.indexOf(",") + 1
            );
        }

        // avatarBase64 là base64 thuần (FE đã bỏ prefix)
        user.setAvatar(avatarBase64);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
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


    public void updateAvatar(Integer userId, String avatarBase64) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id = " + userId));
        if (avatarBase64 == null) return;

        String cleanBase64 = stripBase64Prefix(avatarBase64);

        // validate size
        validateBase64Size(cleanBase64);

        user.setAvatar(cleanBase64);
        userRepository.save(user);
    }

    // Helper method to strip data URL prefix if present
    private String stripBase64Prefix(String base64) {
        if (base64 == null) return null;

        int commaIndex = base64.indexOf(",");
        return commaIndex != -1
                ? base64.substring(commaIndex + 1)
                : base64;
    }

    // Helper method to validate size of base64 string
    private void validateBase64Size(String base64) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        int sizeInBytes = decodedBytes.length;

        if (sizeInBytes > 2 * 1024 * 1024) {
            throw new IllegalArgumentException("Avatar exceeds 2MB");
        }
    }

    // Helper method to validate image type
    private void validateImageType(byte[] imageBytes) throws IOException {
        String mimeType = URLConnection.guessContentTypeFromStream(
                new ByteArrayInputStream(imageBytes)
        );

        if (mimeType == null || !mimeType.startsWith("image/")) {
            throw new IllegalArgumentException("Invalid image format");
        }
    }

    public UserOutputDTO me() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserOutputDTO userOutputDTO = new UserOutputDTO();
        userOutputDTO.setId(user.getId());
        userOutputDTO.setEmail(user.getEmail());
        userOutputDTO.setFullName(user.getFullName());
        userOutputDTO.setPhone(user.getPhone());
        userOutputDTO.setUsername(user.getUserName());
        userOutputDTO.setWallet(user.getWallet());
        return userOutputDTO;
    }


}
