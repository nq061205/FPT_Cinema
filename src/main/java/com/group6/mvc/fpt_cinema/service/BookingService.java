package com.group6.mvc.fpt_cinema.service;

import java.util.List;

import com.group6.mvc.fpt_cinema.dto.request.CreateBookingRequest;
import com.group6.mvc.fpt_cinema.dto.request.ViewBookingHistoryRequest;
import com.group6.mvc.fpt_cinema.dto.response.CreateBookingResponse;
import com.group6.mvc.fpt_cinema.dto.response.ViewBookingHistoryResponse;
import com.group6.mvc.fpt_cinema.entity.Booking;

public interface BookingService extends CrudService<Booking, Integer> {

    List<ViewBookingHistoryResponse> getBookingHistory(Integer customerId, ViewBookingHistoryRequest request);

    CreateBookingResponse createBooking(Integer customerId, CreateBookingRequest request);
}
