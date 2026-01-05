package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.UserProfileDTO;
import com.vti.springdatajpa.dto.UserDto;
import com.vti.springdatajpa.dto.UserProfileUpdateDTO;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



public interface UserService {
    public UserProfileDTO getProfile(String username);

    void updateProfile(String identity, UserProfileUpdateDTO dto);

    void updateAvatar(String identity, String avatarBase64);
}
