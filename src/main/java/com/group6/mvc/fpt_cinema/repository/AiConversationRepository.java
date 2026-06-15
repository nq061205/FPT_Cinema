package com.group6.mvc.fpt_cinema.repository;

import java.util.Optional;

import com.group6.mvc.fpt_cinema.entity.Ai_Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AiConversationRepository extends JpaRepository<Ai_Conversation, Integer> {
    Optional<Ai_Conversation> findByIdAndUserId(
            Integer id,
            Integer userId);
}
