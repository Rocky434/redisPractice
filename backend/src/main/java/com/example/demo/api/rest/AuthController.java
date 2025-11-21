package com.example.demo.api.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.users.UserEntity;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.ObjectRedisService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectRedisService objectRedisService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()));
        UserDetails user = (UserDetails) auth.getPrincipal();
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/setredis")
    public ResponseEntity<?> setRedis(RegisterRequest request) throws JsonProcessingException {
        System.out.println(userRepository.findByUsername("rockyrocky"));
        objectRedisService.set("users::rockyrocky", userRepository.findByUsername("rockyrocky"), 100);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/getredis")
    public ResponseEntity<?> getredis(RegisterRequest request) throws JsonProcessingException {
        // return ResponseEntity.ok(objectRedisService.get("users::rockyrocky",
        // UserEntity.class));
        return ResponseEntity.ok(userService.getUser("rockyrocky"));
    }
}
