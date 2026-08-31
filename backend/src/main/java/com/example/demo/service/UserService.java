package com.example.demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.cache.UserRoleCacheDto;
import com.example.demo.entity.users.RoleEntity;
import com.example.demo.entity.users.UserEntity;
import com.example.demo.entity.users.UserRolesEntity;
import com.example.demo.enums.Role;
import com.example.demo.exception.AppException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.UserRolesRepository;

@Service
@CacheConfig(cacheNames = "users")
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    // ... 保持所有 @Autowired 字段 ...
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRolesRepository userRolesRepository;

    // --- 1. 用戶註冊 (Register) 修正 ---
    public void register(RegisterRequest request) {

        // 1.1 替換 System.out.println
        logger.debug("Starting user registration process for user: {}", request.getUserName());

        String userName = request.getUserName();

        if (userRepository.existsByUsername(userName)) {
            throw new AppException("帳號已存在", HttpStatus.CONFLICT);
        }

        // 創建用戶
        UserEntity user = new UserEntity();
        user.setUsername(userName);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        UserEntity savedUser = userRepository.save(user); // 確保我們使用持久化後的實體

        // 確定角色：如果請求中沒有指定，則默認為 ROLE_USER
        Role requestedRole = request.getRole() != null ? request.getRole() : Role.ROLE_USER;

        // 1.2 修正：使用 orElseThrow 安全獲取 RoleEntity
        RoleEntity roleEntity = roleRepository.findByName(requestedRole)
                .orElseThrow(() -> {
                    logger.error("Required role not found in DB: {}", requestedRole.name());
                    return new AppException("系統配置錯誤：缺少必要的角色實體", HttpStatus.INTERNAL_SERVER_ERROR);
                });

        UserRolesEntity userRolesEntity = new UserRolesEntity();
        userRolesEntity.setUser(savedUser);
        userRolesEntity.setRole(roleEntity);

        userRolesRepository.save(userRolesEntity);
        logger.info("User {} registered successfully with role {}", userName, requestedRole.name());
    }

    @Cacheable(value = "userRole", key = "#username", unless = "#result == null")
    @Transactional(readOnly = true)
    public UserRoleCacheDto getUserRole(String username) {
        UserEntity userEntity = userRepository.findByUsername(username);
        List<String> roleNames = userEntity.getUserRolesEntities()
                .stream()
                .map(userRolesEntitie -> userRolesEntitie.getRole().getName().name())
                .collect(Collectors.toList());
        UserRoleCacheDto userRoleCacheDto = new UserRoleCacheDto();
        userRoleCacheDto.setUsername(userEntity.getUsername());
        userRoleCacheDto.setPassword(userEntity.getPassword());
        userRoleCacheDto.setRoleNames(roleNames);
        return userRoleCacheDto;
    }

}