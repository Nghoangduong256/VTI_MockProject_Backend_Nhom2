package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.User;

import java.util.List;

public interface UserManageService {
    List<User> getAllUsers();

    void lockUser(Integer id);
    void unlockUser(Integer id);

}
