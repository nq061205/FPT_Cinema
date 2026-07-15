package com.group6.mvc.fpt_cinema.service;

import com.group6.mvc.fpt_cinema.dto.request.CheckInTicketRequest;
import com.group6.mvc.fpt_cinema.dto.request.TicketLookupRequest;
import com.group6.mvc.fpt_cinema.dto.response.CheckInTicketResponse;
import com.group6.mvc.fpt_cinema.dto.response.TicketLookupResponse;
import com.group6.mvc.fpt_cinema.entity.Ticket;

public interface TicketService extends CrudService<Ticket, Integer> {
    CheckInTicketResponse checkIn(CheckInTicketRequest request);

    TicketLookupResponse lookup(TicketLookupRequest request);
}
