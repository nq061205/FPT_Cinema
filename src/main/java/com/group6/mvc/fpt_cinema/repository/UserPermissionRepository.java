package com.group6.mvc.fpt_cinema.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group6.mvc.fpt_cinema.entity.User_Permission;

@Repository
public interface UserPermissionRepository extends JpaRepository<User_Permission, Integer> {

    @Query("""
            select userPermission
            from User_Permission userPermission
            join fetch userPermission.permission
            where userPermission.user.id = :userId
            order by userPermission.id
            """)
    List<User_Permission> findAllWithPermissionByUserId(@Param("userId") Integer userId);

    Optional<User_Permission> findByUserIdAndPermissionId(
            Integer userId,
            Integer permissionId);
}
