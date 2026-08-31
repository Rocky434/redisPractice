package com.example.demo.entity.users;

import java.util.List;

import com.example.demo.enums.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "users", name = "role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {

    public RoleEntity(Role name, String describe) {
        this.name = name;
        this.describe = describe;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @Enumerated(EnumType.STRING)
    private Role name;

    @Column(nullable = false, length = 100)
    private String describe;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<UserRolesEntity> UserRolesEntity;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RolePermissionsEntity> rolePermissionsEntities;
}
