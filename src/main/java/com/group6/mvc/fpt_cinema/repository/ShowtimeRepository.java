package com.group6.mvc.fpt_cinema.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group6.mvc.fpt_cinema.entity.Showtime;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Integer> {

    boolean existsByRoomIdAndStatus(Integer roomId, String status); 
    boolean existsByRoomIdAndStatusNotInAndStartTimeAfter(Integer roomId, List<String> statuses, LocalDateTime startTime); 
}
