package com.group6.mvc.fpt_cinema.integration.n8n;

public record N8nChatRequest(
        Integer conversationId,
        Integer userId,
        String message,
        N8nChatContext context) {

    public N8nChatRequest(
            Integer conversationId,
            Integer userId,
            String message) {
        this(conversationId, userId, message, N8nChatContext.empty());
    }
}
