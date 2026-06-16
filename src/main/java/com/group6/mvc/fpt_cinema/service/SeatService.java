package com.group6.mvc.fpt_cinema.service;

import java.util.List;

import com.group6.mvc.fpt_cinema.dto.request.ViewSeatMapRequest;
import com.group6.mvc.fpt_cinema.dto.response.ViewSeatMapResponse;
import com.group6.mvc.fpt_cinema.entity.Seat;

public interface SeatService extends CrudService<Seat, Integer> {
        ViewSeatMapResponse viewSeatListByRoom(Integer roomId, ViewSeatMapRequest request);
}
