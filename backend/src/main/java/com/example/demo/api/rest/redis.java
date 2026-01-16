package com.example.demo.api.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;

@RestController
@RequestMapping("/api/public")
public class redis {
    @Autowired
    private UserService userService;

    // @GetMapping("/getredis")
    // public ResponseEntity<?> getredis(RegisterRequest request) throws
    // JsonProcessingException {
    // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    // String username = auth.getName();
    // System.out.println(username);
    // return ResponseEntity.ok(userService.getUser(username));
    // }
}
