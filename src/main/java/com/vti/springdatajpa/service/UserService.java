package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.UserProfileDTO;
import com.vti.springdatajpa.dto.UserDto;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



public interface UserService {
    public UserDto getUserById(Integer id);

    public void updateUser(Integer id, UserDto userDto);

    public void updateUserAvatar(Integer id, String avatarUrl);

    public void deleteUserById(Integer id);

}
