package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "帳號不能為空")
    @Size(max = 50, message = "帳號長度不能超過50")
    private String username;

    @NotBlank(message = "密碼不能為空")
    @Size(min = 8, max = 20, message = "密碼長度須介於8到20之間")
    private String password;

}