package com.vti.springdatajpa.service.impl;

import com.vti.springdatajpa.dto.UserDto;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.UserRepository;
import com.vti.springdatajpa.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UserDto getUserById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found with id = " + id));
        UserDto dto = new UserDto();

        if (user.getFullName() != null) {
            String[] parts = user.getFullName().split(" ", 2);
            dto.setFirstName(parts[0]);
            dto.setLastName(parts.length > 1 ? parts[1] : "");
        }

        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setAvatar(user.getAvatar());
        dto.setPhone(user.getPhone());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setAddress(user.getAddress());

        return dto;
    }

    @Override
    public void updateUser(UUID id, UserDto userDto) {
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
            user.setUsername(userDto.getUsername());
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
    public void updateUserAvatar(UUID id, String avatarUrl) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setAvatar(avatarUrl);
        userRepository.save(user);
    }

    @Override
    public void deleteUserById(UUID id) {
        if (userRepository.existsById(id)){
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
