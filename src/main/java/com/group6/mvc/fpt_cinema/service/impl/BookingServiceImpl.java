package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.dto.request.ViewBookingHistoryRequest;
import com.group6.mvc.fpt_cinema.dto.response.ViewBookingHistoryResponse;
import com.group6.mvc.fpt_cinema.entity.Booking;
import com.group6.mvc.fpt_cinema.mapper.BookingMapper;
import com.group6.mvc.fpt_cinema.repository.BookingRepository;
import com.group6.mvc.fpt_cinema.service.BookingService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BookingServiceImpl
        extends AbstractCrudService<Booking, Integer>
        implements BookingService {
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    public BookingServiceImpl(BookingRepository repository, BookingMapper mapper) {
        super(repository);
        this.bookingRepository = repository;
        this.bookingMapper = mapper;
    }

    @Override
    public List<ViewBookingHistoryResponse> getBookingHistory(
            Integer customerId,
            ViewBookingHistoryRequest request) {
        int page = request == null || request.getPage() == null ? 0 : Math.max(request.getPage(), 0);
        int size = request == null || request.getSize() == null ? 5 : Math.max(request.getSize(), 1);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Booking> bookingPage = bookingRepository.findByCustomerId(customerId, pageable);

        return bookingPage.getContent()
                .stream()
                .map(bookingMapper::toBookingHistoryResponse)
                .toList();
    }
}
