package com.group6.mvc.fpt_cinema.repository;

import java.util.List;
import java.util.Optional;

import com.group6.mvc.fpt_cinema.entity.Role_Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<Role_Permission, Integer> {

    @Query("""
            select rolePermission
            from Role_Permission rolePermission
            join fetch rolePermission.permission
            where rolePermission.role.id = :roleId
            """)
    List<Role_Permission> findAllWithPermissionByRoleId(@Param("roleId") Integer roleId);

    Optional<Role_Permission> findByRoleIdAndPermissionId(
            Integer roleId,
            Integer permissionId);
}
