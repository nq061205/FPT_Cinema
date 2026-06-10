package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Booking;
import com.group6.mvc.fpt_cinema.repository.BookingRepository;
import com.group6.mvc.fpt_cinema.service.BookingService;
import org.springframework.stereotype.Service;

@Service
public class BookingServiceImpl
        extends AbstractCrudService<Booking, Integer>
        implements BookingService {

    public BookingServiceImpl(BookingRepository repository) {
        super(repository);
    }
}
