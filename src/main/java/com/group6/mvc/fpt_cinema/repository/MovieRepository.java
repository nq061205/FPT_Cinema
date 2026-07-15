package com.group6.mvc.fpt_cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group6.mvc.fpt_cinema.entity.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Integer> {
    
    @Query("""
            SELECT m
            FROM Movie m
            WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    List<Movie> searchByMovieName(@Param("keyword") String keyword);
}
