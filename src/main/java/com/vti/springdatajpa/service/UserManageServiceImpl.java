package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.entity.enums.Role;
import com.vti.springdatajpa.repository.UserManagerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserManageServiceImpl implements  UserManageService {
    private final UserManagerRepository userManagerRepository;

    public UserManageServiceImpl(UserManagerRepository userManagerRepository) {
        this.userManagerRepository = userManagerRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userManagerRepository.findByRoleNot(Role.ADMIN);
    }

    @Override
    public void lockUser(Integer id) {
        userManagerRepository.lockUser(id);
    }

    @Override
    public void unlockUser(Integer id) {
        userManagerRepository.unlockUser(id);
    }
}
