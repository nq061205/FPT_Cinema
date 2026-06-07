package com.group6.mvc.fpt_cinema.repository;

import com.group6.mvc.fpt_cinema.entity.User_Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPermissionRepository extends JpaRepository<User_Permissions, Integer> {
}
