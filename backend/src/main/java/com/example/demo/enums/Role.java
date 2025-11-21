package com.example.demo.enums;

import org.springframework.http.HttpStatus;

import com.example.demo.exception.AppException;
import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    ROLE_USER("普通使用者，擁有基本功能權限"),
    ROLE_STAFF("員工，擁有特定的操作和數據存取權限"),
    ROLE_MANAGER("經理，擁有部門管理和審批權限"),
    ROLE_DIRECTOR("總監，擁有高階管理和系統配置權限");

    private final String describe;

    Role(String describe) {
        this.describe = describe;
    }

    public String getDescribe() {
        return describe;
    }

    @JsonCreator
    public static Role from(String value) {
        for (Role role : Role.values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new AppException("使用者角色錯誤：角色名稱無效", HttpStatus.BAD_REQUEST);
    }
}
