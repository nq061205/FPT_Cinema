package com.group6.mvc.fpt_cinema.integration.n8n;

import java.util.List;

public record N8nChatContext(
        List<N8nMovieContext> movies) {

    public static N8nChatContext empty() {
        return new N8nChatContext(List.of());
    }
}
