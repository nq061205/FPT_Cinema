package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Payment;
import com.group6.mvc.fpt_cinema.repository.PaymentRepository;
import com.group6.mvc.fpt_cinema.service.PaymentService;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl
        extends AbstractCrudService<Payment, Integer>
        implements PaymentService {

    public PaymentServiceImpl(PaymentRepository repository) {
        super(repository);
    }
}
