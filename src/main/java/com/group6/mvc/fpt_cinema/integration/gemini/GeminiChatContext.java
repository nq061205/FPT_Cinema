package com.group6.mvc.fpt_cinema.integration.gemini;

import java.util.List;

public record GeminiChatContext(
        List<GeminiMovieContext> movies,
        List<GeminiChatHistoryMessage> history) {
}
