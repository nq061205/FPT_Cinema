package com.group6.mvc.fpt_cinema.repository;

import java.util.List;

import com.group6.mvc.fpt_cinema.entity.Ai_Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiMessageRepository extends JpaRepository<Ai_Message, Long> {
    List<Ai_Message> findAllByConversationIdOrderByCreatedAtAscIdAsc(
            Integer conversationId);
}
