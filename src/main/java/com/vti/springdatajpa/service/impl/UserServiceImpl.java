package com.vti.springdatajpa.service.impl;

import com.vti.springdatajpa.dto.UserDto;
import com.vti.springdatajpa.dto.UserOutputDTO;
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
import java.util.Base64;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDto getUserById(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id = " + id));
        UserDto dto = new UserDto();

        if (user.getFullName() != null) {
            String[] parts = user.getFullName().split(" ", 2);
            dto.setFirstName(parts[0]);
            dto.setLastName(parts.length > 1 ? parts[1] : "");
        }

        dto.setUsername(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setPhone(user.getPhone());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setAddress(user.getAddress());

        return dto;
    }

    @Override
    public void updateUser(Integer id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id = " + id));

        // Thiết lập những trường cần cập nhật
        if ((userDto.getFirstName() != null && !userDto.getFirstName().isBlank())
                || (userDto.getLastName() != null && !userDto.getLastName().isBlank())) {

            String firstName = userDto.getFirstName() != null
                    ? userDto.getFirstName().trim()
                    : "";

            String lastName = userDto.getLastName() != null
                    ? userDto.getLastName().trim()
                    : "";

            user.setFullName((firstName + " " + lastName).trim());
        }

        if (userDto.getUsername() != null && !userDto.getUsername().isBlank()) {
            user.setUserName(userDto.getUsername());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            user.setEmail(userDto.getEmail());
        }

        if (userDto.getPhone() != null && !userDto.getPhone().isBlank()) {
            user.setPhone(userDto.getPhone());
        }

        if (userDto.getDateOfBirth() != null) {
            user.setDateOfBirth(userDto.getDateOfBirth());
        }

        if (userDto.getAddress() != null && !userDto.getAddress().isBlank()) {
            user.setAddress(userDto.getAddress());
        }

        userRepository.save(user);
    }

    @Override
    public void updateUserAvatar(Integer id, String avatarUrl) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setAvatar(avatarUrl);
        userRepository.save(user);
    }

    @Override
    public void deleteUserById(Integer id) {
        if (userRepository.existsById(id)){
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User not found");
        }
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
