package com.example.demo.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.exception.AppException;
import com.example.demo.security.jwt.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private ObjectRedisService objectRedisService;

    public LoginResponse login(LoginRequest loginRequest) throws JsonProcessingException {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()));

        UserDetails user = (UserDetails) auth.getPrincipal();

        String accessToken = jwtUtil.generateToken(user.getUsername());

        String refreshToken = UUID.randomUUID().toString();
        objectRedisService.set("refresh:" + refreshToken, user.getUsername(), 360, TimeUnit.SECONDS);

        return new LoginResponse(accessToken, refreshToken);
    }

    public String refreshAccessToken(String refreshToken) throws JsonMappingException, JsonProcessingException {
        String username = objectRedisService.get("refresh:" + refreshToken, String.class);

        if (username == null) {
            return null;
        }

        return jwtUtil.generateToken(username);
    }
}
