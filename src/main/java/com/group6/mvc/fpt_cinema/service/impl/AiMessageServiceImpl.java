package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Ai_Message;
import com.group6.mvc.fpt_cinema.repository.AiMessageRepository;
import com.group6.mvc.fpt_cinema.service.AiMessageService;
import org.springframework.stereotype.Service;

@Service
public class AiMessageServiceImpl
        extends AbstractCrudService<Ai_Message, Long>
        implements AiMessageService {

    public AiMessageServiceImpl(AiMessageRepository repository) {
        super(repository);
    }
}
