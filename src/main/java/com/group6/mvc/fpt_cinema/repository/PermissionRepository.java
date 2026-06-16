package com.group6.mvc.fpt_cinema.repository;

import java.util.Optional;

import com.group6.mvc.fpt_cinema.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Integer> {
    Optional<Permission> findByPermissionCodeIgnoreCase(String permissionCode);
}
