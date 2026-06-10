package com.group6.mvc.fpt_cinema.service.impl;

import com.group6.mvc.fpt_cinema.entity.Ai_Conversation;
import com.group6.mvc.fpt_cinema.repository.AiConversationRepository;
import com.group6.mvc.fpt_cinema.service.AiConversationService;
import org.springframework.stereotype.Service;

@Service
public class AiConversationServiceImpl
        extends AbstractCrudService<Ai_Conversation, Integer>
        implements AiConversationService {

    public AiConversationServiceImpl(AiConversationRepository repository) {
        super(repository);
    }
}
