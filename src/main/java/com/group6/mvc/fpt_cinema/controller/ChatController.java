package com.group6.mvc.fpt_cinema.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group6.mvc.fpt_cinema.apiresponse.ApiResponse;
import com.group6.mvc.fpt_cinema.dto.request.ChatMessageRequest;
import com.group6.mvc.fpt_cinema.dto.response.ChatConversationResponse;
import com.group6.mvc.fpt_cinema.dto.response.ChatMessageResponse;
import com.group6.mvc.fpt_cinema.dto.response.ChatReplyResponse;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.service.ChatService;

@RestController
@RequestMapping("/api/chat/conversations")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ApiResponse<ChatConversationResponse> createConversation(
            @AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<ChatConversationResponse>builder()
                .message("Conversation created successfully")
                .result(chatService.createConversation(getUserId(jwt)))
                .build();
    }

    @PostMapping("/{conversationId}/messages")
    public ApiResponse<ChatReplyResponse> sendMessage(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer conversationId,
            @RequestBody(required = false) ChatMessageRequest request) {
        return ApiResponse.<ChatReplyResponse>builder()
                .message("Message sent successfully")
                .result(chatService.sendMessage(
                        getUserId(jwt),
                        conversationId,
                        request == null ? null : request.getMessage()))
                .build();
    }

    @GetMapping("/{conversationId}/messages")
    public ApiResponse<List<ChatMessageResponse>> getMessages(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer conversationId) {
        return ApiResponse.<List<ChatMessageResponse>>builder()
                .message("Messages retrieved successfully")
                .result(chatService.getMessages(
                        getUserId(jwt),
                        conversationId))
                .build();
    }

    @PutMapping("/{conversationId}/close")
    public ApiResponse<ChatConversationResponse> closeConversation(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Integer conversationId) {
        return ApiResponse.<ChatConversationResponse>builder()
                .message("Conversation closed successfully")
                .result(chatService.closeConversation(
                        getUserId(jwt),
                        conversationId))
                .build();
    }

    private Integer getUserId(Jwt jwt) {
        if (jwt == null) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        Object userId = jwt.getClaim("userId");
        if (userId instanceof Number number) {
            return number.intValue();
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }
}
