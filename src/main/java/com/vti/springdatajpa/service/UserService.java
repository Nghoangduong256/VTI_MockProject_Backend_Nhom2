package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.UserDto;
import com.vti.springdatajpa.entity.User;

import java.util.UUID;

public interface UserService {
    public UserDto getUserById(UUID id);

    public void updateUser(UUID id, UserDto userDto);

    public void updateUserAvatar(UUID id, String avatarUrl);

    public void deleteUserById(UUID id);

}
