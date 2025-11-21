package com.example.demo.dto.cache;

import java.io.Serializable; // 推荐实现 Serializable 接口
import java.util.ArrayList;
import java.util.List;
import com.example.demo.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; // 新增：Jackson 反序列化通常需要無參建構子

@Data
@NoArgsConstructor // 新增無參建構子
@AllArgsConstructor // 方便在 Service 層建立實例
public class UserCacheDto implements Serializable {
    // 推薦實現 Serializable 接口，以防未來切換序列化方式

    private Long userId;

    private String username;

    // 將集合類型用來存放角色的 List
    private List<Role> roles = new ArrayList<>();
    // 註解：將欄位名稱從 role 改為 roles，更符合 List 集合的命名習慣

    // 注意：這裡不應有 @NotBlank 或 @Size 等驗證註解
    // 因為這是應用程式內部創建的數據，不是外部輸入。
}