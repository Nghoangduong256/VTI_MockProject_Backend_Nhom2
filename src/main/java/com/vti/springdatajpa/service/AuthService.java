package com.vti.springdatajpa.service;

import com.vti.springdatajpa.dto.LoginRequest;
import com.vti.springdatajpa.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}