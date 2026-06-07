package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Tickets;
import com.group6.mvc.fpt_cinema.repository.TicketRepository;
import com.group6.mvc.fpt_cinema.service.TicketService;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl
        extends AbstractCrudService<Tickets, Integer>
        implements TicketService {

    public TicketServiceImpl(TicketRepository repository) {
        super(repository);
    }
}
