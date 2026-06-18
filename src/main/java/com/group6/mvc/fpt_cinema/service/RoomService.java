package com.group6.mvc.fpt_cinema.service;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.group6.mvc.fpt_cinema.dto.request.RoomRequest;
import com.group6.mvc.fpt_cinema.dto.response.RoomResponse;
import com.group6.mvc.fpt_cinema.entity.Room;

public interface RoomService extends CrudService<Room, Integer> {

    RoomResponse createRoom(RoomRequest request);
    RoomResponse updateRoom(Integer id, RoomRequest request);
    void deleteRoom(Integer id);
    Page<RoomResponse> getAllRooms(String status, String roomType, Pageable pageable);

    RoomResponse getRoomById(Integer id);
    void updateRoomStatus(Integer id, String status);
}
