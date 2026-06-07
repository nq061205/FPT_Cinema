package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Rooms;
import com.group6.mvc.fpt_cinema.repository.RoomRepository;
import com.group6.mvc.fpt_cinema.service.RoomService;
import org.springframework.stereotype.Service;

@Service
public class RoomServiceImpl
        extends AbstractCrudService<Rooms, Integer>
        implements RoomService {

    public RoomServiceImpl(RoomRepository repository) {
        super(repository);
    }
}
