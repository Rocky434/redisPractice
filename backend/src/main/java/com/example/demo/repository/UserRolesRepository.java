package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.entity.users.UserRolesEntity;
import com.example.demo.enums.Role;

public interface UserRolesRepository extends JpaRepository<UserRolesEntity, Long> {

    UserRolesEntity findByRole(Role role);

    @EntityGraph(attributePaths = { "role" })
    List<UserRolesEntity> findByUserIdIn(List<Long> userIds);
}
