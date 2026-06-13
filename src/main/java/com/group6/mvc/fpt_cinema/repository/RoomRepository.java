package com.group6.mvc.fpt_cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group6.mvc.fpt_cinema.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    boolean existsByRoomName(String roomName); 
    boolean existsByRoomNameAndIdNot(String roomName, Integer id); 

    List<Room> findByStatus(String status);
    List<Room> findByRoomType(String roomType); 
}
