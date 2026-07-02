package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.dto.request.RoomRequest;
import com.group6.mvc.fpt_cinema.dto.response.RoomResponse;
import com.group6.mvc.fpt_cinema.entity.Room;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.enums.ShowtimeStatus;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.mapper.IRoomMapper;
import com.group6.mvc.fpt_cinema.repository.RoomRepository;
import com.group6.mvc.fpt_cinema.repository.SeatRepository;
import com.group6.mvc.fpt_cinema.repository.ShowtimeRepository;
import com.group6.mvc.fpt_cinema.service.RoomService;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl
        extends AbstractCrudService<Room, Integer>
        implements RoomService {

    private final RoomRepository roomRepository;


    private ShowtimeRepository showtimeRepository;


    private SeatRepository seatRepository;


    private IRoomMapper IRoomMapper;

    public RoomServiceImpl(JpaRepository<Room, Integer> repository,
                           RoomRepository roomRepository,
                           ShowtimeRepository showtimeRepository,
                           SeatRepository seatRepository,
                           IRoomMapper IRoomMapper) {
        super(repository);
        this.roomRepository = roomRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
        this.IRoomMapper = IRoomMapper;
    }

    @Override
    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        if(request.getRoomName() == null || request.getRoomName().trim().isEmpty()){
            throw new AppException(ErrorCode.ROOM_NAME_BLANK);
        }

        if(!isValidRoomType(request.getRoomType())){
            throw new AppException(ErrorCode.INVALID_ROOM_TYPE);
        }

        if(roomRepository.existsByRoomName(request.getRoomName().trim())){
            throw new AppException(ErrorCode.ROOM_NAME_EXIST);
        }

        if(request.getRoomName().trim().length()> 100){
            throw new AppException(ErrorCode.ROOM_NAME_TOO_LONG);
        }

        Room room = IRoomMapper.toEntity(request);
        room.setRoomName(request.getRoomName().trim());
        Room saved = roomRepository.save(room);
        return toResponse(saved);
    }

    private RoomResponse toResponse(Room room) {
        RoomResponse response = IRoomMapper.toResponse(room);
        int seatCount = seatRepository.countByRoomId(room.getId());
        response.setSeatCount(seatCount);
        return response;

    }

    private boolean isValidRoomType(String type) {
        return type != null && List.of("STANDARD", "VIP", "IMAX", "FOUR_DX", "DOLBY")
        .contains(type.toUpperCase());
    }

    @Override
    @Transactional
    public RoomResponse updateRoom(Integer id, RoomRequest request) {
        Room room = roomRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.ROOM_NOT_FOUND));

        if(request.getRoomName() != null && !request.getRoomName().trim().isEmpty()){
            String newName = request.getRoomName().trim();
            if(!newName.equals(room.getRoomName()) && roomRepository.existsByRoomNameAndIdNot(newName, id)){
                throw new AppException(ErrorCode.ROOM_NAME_EXIST);
            }

            room.setRoomName(newName);
        }

        if(request.getRoomType() != null){
            if(!isValidRoomType(request.getRoomType())){
                throw new AppException(ErrorCode.INVALID_ROOM_TYPE);
            }

            room.setRoomType(request.getRoomType());
        }

        Room saved = roomRepository.save(room);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void updateRoomStatus(Integer id, String status){
        Room room = roomRepository.findById(id)
        .orElseThrow(()-> new AppException(ErrorCode.ROOM_NOT_FOUND));

        if(status == null || !List.of("ACTIVE", "MAINTENANCE", "CLOSED")
            .contains(status.toUpperCase())){
                    throw new AppException(ErrorCode.INVALID_ROOM_STATUS);

        }

        if("CLOSED".equalsIgnoreCase(status)){
            if(showtimeRepository.existsByRoomIdAndStatusNotInAndStartTimeAfter(id, List.of(ShowtimeStatus.CANCELLED, ShowtimeStatus.FINISHED), LocalDateTime.now())){
                throw new AppException(ErrorCode.ROOM_HAS_ACTIVE_SHOWTIMES);
            }
        }

        room.setStatus(status.toUpperCase());
        roomRepository.save(room);
    }

    @Override
    @Transactional
    public void deleteRoom(Integer id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        if(showtimeRepository.existsByRoomIdAndStatusNotInAndStartTimeAfter(id, List.of(ShowtimeStatus.CANCELLED, ShowtimeStatus.FINISHED), LocalDateTime.now())){
            throw new AppException(ErrorCode.ROOM_HAS_ACTIVE_SHOWTIMES);
        }

        room.setStatus("CLOSED");
        roomRepository.save(room);
    }

    @Override
    public Page<RoomResponse> getAllRooms(String status, String roomType, Pageable pageable) {
        Page<Room> roomPage;

        if(status != null && !status.isEmpty()){
            roomPage = roomRepository.findByStatus(status.toUpperCase(), pageable);

        }else if(roomType != null && !roomType.isEmpty()){
            roomPage = roomRepository.findByRoomType(roomType.toUpperCase(), pageable);
        }else {
            roomPage = roomRepository.findAll(pageable);
        }

        return roomPage.map(this::toResponse);
    }

    @Override
    public RoomResponse getRoomById(Integer id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        return toResponse(room);
    }


}
