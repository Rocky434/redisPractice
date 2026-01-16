package com.example.demo.dto.cache;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleCacheDto implements Serializable {
    private String username;

    private String password;

    private List<String> roleNames;

}