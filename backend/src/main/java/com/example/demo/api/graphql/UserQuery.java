package com.example.demo.api.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.example.demo.entity.users.UserEntity;
import com.example.demo.repository.UserRepository;

@Controller
public class UserQuery {

    @Autowired
    private UserRepository userRepository;

    @QueryMapping
    public UserEntity me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return userRepository.findByUsername(username);

    }
}