package com.group6.mvc.fpt_cinema.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatMessageResponse {
    private Long id;
    private String sender;
    private String content;
    private String intent;
    private LocalDateTime createdAt;
}
