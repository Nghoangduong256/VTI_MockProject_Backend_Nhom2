package com.vti.springdatajpa.service.impl;

import com.vti.springdatajpa.dto.LoginRequest;
import com.vti.springdatajpa.dto.LoginResponse;
import com.vti.springdatajpa.entity.User;
import com.vti.springdatajpa.exception.AuthException;
import com.vti.springdatajpa.repository.UserRepository;
import com.vti.springdatajpa.security.JwtUtil;
import com.vti.springdatajpa.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new AuthException("Invalid username or password"));

        if (!user.isActive()) {
            throw new AuthException("User is inactive");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user);

        return new LoginResponse(
                token,
                "Bearer",
                jwtUtil.getExpirationSeconds(),
                user.getUserName(),
                user.getEmail(),
                user.getFullName(),
                java.util.Collections.singletonList(user.getRole().name()));

    }
}
