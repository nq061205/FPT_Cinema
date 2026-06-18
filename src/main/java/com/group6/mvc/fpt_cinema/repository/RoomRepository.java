package com.group6.mvc.fpt_cinema.repository;

import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group6.mvc.fpt_cinema.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    boolean existsByRoomName(String roomName); 
    boolean existsByRoomNameAndIdNot(String roomName, Integer id); 

    Page<Room> findByStatus(String status, Pageable pageable);
    Page<Room> findByRoomType(String roomType, Pageable pageable); 
}
