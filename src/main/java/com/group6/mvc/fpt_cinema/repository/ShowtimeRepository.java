package com.group6.mvc.fpt_cinema.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.group6.mvc.fpt_cinema.entity.Showtime;
import com.group6.mvc.fpt_cinema.enums.ShowtimeStatus;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Integer> {

    @Query(value = "SELECT s.* FROM showtimes s " +
        "JOIN movies m ON s.movie_id = m.id " +
            "WHERE s.room_id = :roomId " +
            "AND s.status != 'CANCELLED' " +
            "AND DATE_ADD(s.start_time, INTERVAL (m.duration_minutes + s.cleaning_buffer_minutes) MINUTE) > NOW()",
            nativeQuery = true
    )
    List<Showtime> findActiveShowtimesByRoomId(@Param("roomId") Integer roomId);

    @Query("""
        SELECT s FROM Showtime s
        WHERE (:movieId IS NULL OR s.movie.id = :movieId)
        AND (:roomId IS NULL OR s.room.id = :roomId)
        AND (:startDate IS NULL OR s.startTime >= :startDate)
        AND (:endDate IS NULL OR s.startTime < :endDate)
        AND (:targetStatus IS NULL OR s.status = :targetStatus)
        ORDER BY s.startTime ASC
    """)
    Page<Showtime> findFiltered(
            @Param("movieId") Integer movieId,
            @Param("roomId") Integer roomId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("targetStatus") String targetStatus,
            Pageable pageable
    );
 
    @Modifying
    @Transactional
    @Query("UPDATE Showtime s SET s.status = com.group6.mvc.fpt_cinema.enums.ShowtimeStatus.FINISHED WHERE s.id = :id")
    void updateStatusToFinished(@Param("id") Integer id);


//Lấy các suất chiếu trong quá khứ da end nhưng chưa đ cddanhs dấu là finished
    @Modifying
    @Transactional
    @Query(value = """
        UPDATE showtimes s
        JOIN movies m ON s.movie_id = m.id
        SET s.status = 'FINISHED'
        WHERE s.status NOT IN ('CANCELLED', 'FINISHED')
        AND DATE_ADD(s.start_time, INTERVAL m.duration_minutes MINUTE) < :now
    """, nativeQuery = true)
    int updateMissedShowtimes(@Param("now") LocalDateTime now);

    //lay cac suat chieu trog tuong lai de dang ky lại lịch động
    @Query(value = """
        SELECT s.* FROM showtimes s
        JOIN movies m ON s.movie_id = m.id
        WHERE s.status NOT IN ('CANCELLED', 'FINISHED')
        AND DATE_ADD(s.start_time, INTERVAL m.duration_minutes MINUTE) > :now
    """, nativeQuery = true)
    List<Showtime> findFutureActiveShowtimes(@Param("now") LocalDateTime now);

    
    boolean existsByRoomIdAndStatusNotInAndStartTimeAfter(
            Integer roomId,
            List<ShowtimeStatus> statuses,
            LocalDateTime dateTime
    );
}
