package com.example.demo.api.rest;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtUtil;
import com.example.demo.service.AuthService;
import com.example.demo.service.ObjectRedisService;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest)
            throws JsonProcessingException {

        // service 回傳登入結果（accessToken + refreshToken Cookie）
        LoginResponse loginResponse = authService.login(loginRequest);

        // 建立 ResponseCookie
        ResponseCookie cookie = ResponseCookie.from("refreshToken", loginResponse.refreshToken())
                .httpOnly(true)
                .secure(false) // 正式環境請改 true
                .path("/auth")
                .maxAge(Duration.ofDays(7))
                .sameSite("Strict")
                .build();

        // 回傳 Access Token 並附帶 Set-Cookie header
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("token", loginResponse.accessToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken)
            throws JsonMappingException, JsonProcessingException {

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Refresh token not found"));
        }

        // 產生新的 Access Token
        String newAccessToken = authService.refreshAccessToken(refreshToken);

        if (newAccessToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or expired refresh token"));
        }

        return ResponseEntity.ok()
                .body(Map.of("token", newAccessToken));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok("success");
    }

    @GetMapping("/setredis")
    public ResponseEntity<?> setRedis(RegisterRequest request) throws JsonProcessingException {
        System.out.println(userRepository.findByUsername("rockyrocky"));
        objectRedisService.set("users::rockyrocky", userRepository.findByUsername("rockyrocky"), 100, TimeUnit.SECONDS);
        return ResponseEntity.ok("success");
    }

}
