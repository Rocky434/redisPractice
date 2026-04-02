package com.example.demo.api.graphql;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.example.demo.entity.users.RoleEntity;
import com.example.demo.entity.users.UserEntity;
import com.example.demo.entity.users.UserRolesEntity;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRolesRepository;

@Controller
public class UserQuery {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRolesRepository userRolesRepository;

    @QueryMapping
    public UserEntity me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return userRepository.findByUsername(username);

    }

    @QueryMapping
    public List<UserEntity> users() {
        return userRepository.findAll();
    }

    @BatchMapping(typeName = "User", field = "roles") // 注意 field 名稱要跟 Schema 一致
    public Map<UserEntity, List<RoleEntity>> roles(List<UserEntity> users) {

        // 1. 收集所有 userId
        List<Long> userIds = users.stream().map(UserEntity::getId).toList();

        // 2. 一次查出所有 UserRoles (記得加 @EntityGraph 抓 role)
        List<UserRolesEntity> userRoles = userRolesRepository.findByUserIdIn(userIds);

        // 3. 核心邏輯：根據 UserId 進行分組 (Grouping)
        // 這會產生一個 Map<Long, List<UserRolesEntity>>
        Map<Long, List<RoleEntity>> roleListMap = userRoles.stream()
                .collect(Collectors.groupingBy(
                        ur -> ur.getUser().getId(), // 分組依據：UserId
                        Collectors.mapping(UserRolesEntity::getRole, Collectors.toList())));

        // 4. 回傳 Map<UserEntity, List<RoleEntity>>
        Map<UserEntity, List<RoleEntity>> resultMap = new HashMap<>();
        for (UserEntity user : users) {
            // 如果該用戶沒有任何角色，回傳空列表而不是 null，對 GraphQL 比較友善
            resultMap.put(user, roleListMap.getOrDefault(user.getId(), Collections.emptyList()));
        }

        return resultMap;
    }
}