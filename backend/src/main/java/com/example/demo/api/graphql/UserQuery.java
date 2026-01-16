package com.example.demo.api.graphql;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.example.demo.entity.users.RoleEntity;
import com.example.demo.entity.users.UserEntity;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;

@Controller
public class UserQuery {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @QueryMapping
    public UserEntity me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return userRepository.findByUsername(username);

    }

    // @BatchMapping(typeName = "User", field = "role")
    // public Map<UserEntity, RoleEntity> role(List<UserEntity> users) {

    // // 1. 收集所有 roleId
    // List<Long> roleIds = users.stream()
    // .map(u -> u.getId())
    // .distinct()
    // .toList();

    // // 2. 一次查所有 Role
    // List<Role> roles = roleRepository.findByIdIn(roleIds);

    // // 3. 轉成 Map<id, Role>
    // Map<Long, Role> roleMap = roles.stream()
    // .collect(Collectors.toMap(Role::getId, r -> r));

    // // 4. 回傳 GraphQL 需要的 Map<來源物件, 結果>
    // return users.stream()
    // .collect(Collectors.toMap(
    // u -> u,
    // u -> roleMap.get(u.getRole().getId())
    // ));
    // }
}