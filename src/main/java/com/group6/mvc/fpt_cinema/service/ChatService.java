package com.group6.mvc.fpt_cinema.service;

import java.util.List;

import com.group6.mvc.fpt_cinema.dto.response.ChatConversationResponse;
import com.group6.mvc.fpt_cinema.dto.response.ChatMessageResponse;
import com.group6.mvc.fpt_cinema.dto.response.ChatReplyResponse;

public interface ChatService {
    ChatConversationResponse createConversation(Integer userId);

    ChatReplyResponse sendMessage(
            Integer userId,
            Integer conversationId,
            String message);

    List<ChatMessageResponse> getMessages(
            Integer userId,
            Integer conversationId);

    ChatConversationResponse closeConversation(
            Integer userId,
            Integer conversationId);
}
