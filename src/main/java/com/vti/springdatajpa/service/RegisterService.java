package com.vti.springdatajpa.service;

import com.vti.springdatajpa.entity.User;

public interface RegisterService {
    User createAccount(User user);
}
