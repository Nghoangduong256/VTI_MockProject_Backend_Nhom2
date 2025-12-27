package com.vti.springdatajpa.service.impl;

import com.vti.springdatajpa.dto.UserOutputDTO;
import com.vti.springdatajpa.entity.*;
import com.vti.springdatajpa.repository.*;
import com.vti.springdatajpa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public UserOutputDTO me() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserOutputDTO userOutputDTO = new UserOutputDTO();
        userOutputDTO.setId(user.getId());
        userOutputDTO.setEmail(user.getEmail());
        userOutputDTO.setFullName(user.getFullName());
        userOutputDTO.setPhone(user.getPhone());
        userOutputDTO.setUsername(user.getUsername());
        userOutputDTO.setWallet(user.getWallet());
        return userOutputDTO;
    }
}
