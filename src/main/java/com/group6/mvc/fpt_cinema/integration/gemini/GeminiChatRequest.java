package com.group6.mvc.fpt_cinema.integration.gemini;

public record GeminiChatRequest(
        String message,
        GeminiChatContext context) {
}
