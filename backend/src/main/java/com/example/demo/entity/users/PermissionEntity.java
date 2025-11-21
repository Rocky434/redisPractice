package com.example.demo.entity.users;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(schema = "users", name = "permission")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, unique = true)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String describe;

    @OneToMany(mappedBy = "permission", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<RolePermissionsEntity> rolePermissionsEntity;
}