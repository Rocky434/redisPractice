package com.example.demo.service;

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

import com.example.demo.entity.users.UserEntity;
import com.example.demo.entity.users.UserRolesEntity;
import com.example.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username);

        List<GrantedAuthority> roleNames = userEntity.getUserRolesEntities()
                .stream()
                .map(userRolesEntitie -> new SimpleGrantedAuthority(userRolesEntitie.getRole().getName().name()))
                .collect(Collectors.toList());

        return User.builder()
                .username(userEntity.getUsername())
                .password(userEntity.getPassword())
                .authorities(roleNames)
                .build();
    }
}
