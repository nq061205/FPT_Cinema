package com.group6.mvc.fpt_cinema.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatConversationResponse {
    private Integer id;
    private String channel;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
