package com.group6.mvc.fpt_cinema.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group6.mvc.fpt_cinema.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByFullName(String username);

    Optional<User> findByEmail(String email);

    User findByPhone(String phoneNumber);

    User getUserByEmail(String email);

    User getUserByPhone(String phone);

    Optional<User> findOneByEmailIgnoreCase(String email);

    @Query("""
            select u
            from User u
            join fetch u.role
            order by u.id
            """)
    List<User> findAllWithRoleOrderById();

    @Query("""
            select u
            from User u
            join fetch u.role
            where u.id = :id
            """)
    Optional<User> findByIdWithRole(@Param("id") Integer id);

    @Query("select u.tokensValidFrom from User u where u.id = :id")
    Optional<Instant> findTokensValidFromById(@Param("id") Integer id);
}
