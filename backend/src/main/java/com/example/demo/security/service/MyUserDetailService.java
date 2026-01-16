package com.example.demo.security.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.dto.cache.UserRoleCacheDto;
import com.example.demo.service.UserService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserRoleCacheDto userRoleCacheDto = userService.getUserRole(username);
        System.out.println("loadUserByUsername");

        List<GrantedAuthority> roleNames = userRoleCacheDto.getRoleNames()
                .stream()
                .map(roleName -> new SimpleGrantedAuthority(roleName))
                .collect(Collectors.toList());

        return User.builder()
                .username(userRoleCacheDto.getUsername())
                .password(userRoleCacheDto.getPassword())
                .authorities(roleNames)
                .build();
    }
}
