package com.group6.mvc.fpt_cinema.repository;

import com.group6.mvc.fpt_cinema.entity.Seat;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {

    int countByRoomId(Integer roomId); 

    Page<Seat> findByRoomId(Integer roomId, Pageable pageable); 
    Page<Seat> findByRoomIdAndStatus(Integer roomId, String status, Pageable pageable); 
    Page<Seat> findByRoomIdAndSeatType(Integer roomId, String seatType, Pageable pageable);
    
    Page<Seat> findByRoomIdAndStatusAndSeatType(Integer roomId, String status, String seatType, Pageable pageable);

    List<Seat> findByRoomId(Integer roomId); 

}