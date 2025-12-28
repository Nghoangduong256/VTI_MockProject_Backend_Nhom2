package com.vti.springdatajpa.service.impl;

import com.vti.springdatajpa.dto.UserOutputDTO;
import com.vti.springdatajpa.entity.*;
import com.vti.springdatajpa.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

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
