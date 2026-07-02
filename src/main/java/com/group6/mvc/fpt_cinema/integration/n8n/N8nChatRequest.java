package com.group6.mvc.fpt_cinema.integration.n8n;

public record N8nChatRequest(
        String sessionId,
        Integer conversationId,
        Integer userId,
        String message,
        N8nChatContext context) {

    public N8nChatRequest(
            Integer conversationId,
            Integer userId,
            String message) {
        this(
                conversationId == null ? null : String.valueOf(conversationId),
                conversationId,
                userId,
                message,
                N8nChatContext.empty());
    }

    public N8nChatRequest(
            Integer conversationId,
            Integer userId,
            String message,
            N8nChatContext context) {
        this(
                conversationId == null ? null : String.valueOf(conversationId),
                conversationId,
                userId,
                message,
                context);
    }
}
