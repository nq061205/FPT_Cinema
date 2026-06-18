package com.group6.mvc.fpt_cinema.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatReplyResponse {
    private Integer conversationId;
    private String answer;
    private String intent;
}
