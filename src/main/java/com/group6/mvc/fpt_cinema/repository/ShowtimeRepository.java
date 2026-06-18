package com.group6.mvc.fpt_cinema.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group6.mvc.fpt_cinema.entity.Showtime;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Integer> {

    boolean existsByRoomIdAndStatus(Integer roomId, String status); 
    boolean existsByRoomIdAndStatusNotInAndStartTimeAfter(Integer roomId, List<String> statuses, LocalDateTime startTime); 

    List<Showtime> findByRoomIdAndStatusNotIn(Integer roomId, List<String> statuses); 

    List<Showtime> findByMovieIdAndStatusNotInOrderByStartTimeAsc(Integer movieId, List<String> statuses); 

    List<Showtime> findByStatusNotInOrderByStartTimeAsc(List<String> statuses); 

    @Query("SELECT s FROM Showtime s WHERE s.status NOT IN :excludedStatuses AND(:movieId IS NULL OR s.movie.id = :movieId) "
        + "AND (:roomId IS NULL OR s.room.id = :roomId) "
        + "AND (CAST(:startDate AS timestamp) IS NULL OR s.startTime >= :startDate) "
        + "AND (CAST(:endDate AS timestamp) IS NULL OR s.startTime < :endDate) "
        + "ORDER BY s.startTime ASC"
    )Page<Showtime> findFiltered(
        @Param("excludedStatuses") List<String> excludedStatuses, 
        @Param("movieId") Integer movieId, 
        @Param("roomId") Integer roomId, 
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate, 
        Pageable pageable
    ); 
}
