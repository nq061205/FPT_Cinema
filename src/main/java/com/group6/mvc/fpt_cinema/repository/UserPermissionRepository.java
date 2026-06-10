package com.group6.mvc.fpt_cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group6.mvc.fpt_cinema.entity.User_Permission;

@Repository
public interface UserPermissionRepository extends JpaRepository<User_Permission, Integer> {
}
