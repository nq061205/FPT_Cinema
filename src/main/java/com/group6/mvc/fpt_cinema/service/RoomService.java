package com.group6.mvc.fpt_cinema.service;

import java.util.List;

import com.group6.mvc.fpt_cinema.dto.request.room.RoomRequest;
import com.group6.mvc.fpt_cinema.dto.response.room.RoomResponse;
import com.group6.mvc.fpt_cinema.entity.Room;

public interface RoomService extends CrudService<Room, Integer> {

    RoomResponse createRoom(RoomRequest request); 
    RoomResponse updateRoom(Integer id, RoomRequest request); 
    void deleteRoom(Integer id); 
    List<RoomResponse> getAllRooms(String status, String roomType); 

    RoomResponse getRoomById(Integer id); 
    void updateRoomStatus(Integer id, String status); 
}
