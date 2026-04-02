package com.example.demo.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.demo.entity.users.RoleEntity;
import com.example.demo.enums.Role;
import com.example.demo.repository.RoleRepository;

// 正式版刪除，這個類別的目的是在應用程式啟動時自動檢查並初始化角色資料到資料庫中，確保系統有預設的角色可供使用。
@Component
public class RoleInitializer {

    private final RoleRepository roleRepository;

    public RoleInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    // 監聽 ApplicationReadyEvent，確保所有 Bean 都已載入
    @EventListener(ApplicationReadyEvent.class)
    public void initRoles() {

        List<Role> allRoles = Arrays.asList(Role.values());

        for (Role roleEnum : allRoles) {
            // 1. 檢查角色是否已經存在於資料庫中
            if (roleRepository.findByName(roleEnum).isEmpty()) {

                // 2. 如果不存在，創建並保存 Entity
                RoleEntity role = new RoleEntity(
                        roleEnum,
                        roleEnum.getDescribe());

                roleRepository.save(role);
                System.out.println("角色已創建: " + roleEnum.name());
            }
        }
    }
}