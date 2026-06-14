package com.group6.mvc.fpt_cinema.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.room.RoomRequest;
import com.group6.mvc.fpt_cinema.dto.request.room.RoomStatusRequest;
import com.group6.mvc.fpt_cinema.dto.response.room.RoomResponse;
import com.group6.mvc.fpt_cinema.service.RoomService;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService; 

    public RoomController(RoomService roomService){
        this.roomService = roomService;
    }

    @GetMapping
    public ApiResponse<List<RoomResponse>> getAllRomm(
        @RequestParam(required = false) String status, 
        @RequestParam(required = false) String roomType
    ){
        return ApiResponse.<List<RoomResponse>>builder()
        .message("Room retrieved successfully")
        .result(roomService.getAllRooms(status, roomType))
        .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<RoomResponse> getRoomById(@PathVariable Integer id){
        return ApiResponse.<RoomResponse>builder()
        .message("Room retrieved successfully")
        .result(roomService.getRoomById(id))
        .build(); 
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<RoomResponse> creatRoom(@RequestBody RoomRequest request){
        return ApiResponse.<RoomResponse>builder()
        .message("Room created successfully")
        .result(roomService.createRoom(request))
        .build(); 
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<RoomResponse> updateRoom(
        @PathVariable Integer id, 
        @RequestBody RoomRequest request
    ){
        return ApiResponse.<RoomResponse>builder()
        .message("Room updated successfully")
        .result(roomService.updateRoom(id, request))
        .build(); 
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> deleteRoom(@PathVariable Integer id){
        roomService.deleteRoom(id);
        return ApiResponse.<Void>builder()
        .message("Room closed successfully")
        .build(); 
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('MANAGER')")
    public ApiResponse<Void> updateRoomStatus(
        @PathVariable Integer id, 
        @RequestBody RoomStatusRequest request
    ){
        roomService.updateRoomStatus(id, request.getStatus());
        return ApiResponse.<Void>builder()
        .message("Room status updated successfully")
        .build(); 
    }
}
