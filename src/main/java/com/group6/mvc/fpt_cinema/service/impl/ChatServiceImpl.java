package com.group6.mvc.fpt_cinema.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group6.mvc.fpt_cinema.dto.response.ChatConversationResponse;
import com.group6.mvc.fpt_cinema.dto.response.ChatMessageResponse;
import com.group6.mvc.fpt_cinema.dto.response.ChatReplyResponse;
import com.group6.mvc.fpt_cinema.entity.Ai_Conversation;
import com.group6.mvc.fpt_cinema.entity.Ai_Message;
import com.group6.mvc.fpt_cinema.entity.User;
import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.group6.mvc.fpt_cinema.integration.n8n.N8nChatClient;
import com.group6.mvc.fpt_cinema.integration.n8n.N8nChatContext;
import com.group6.mvc.fpt_cinema.integration.n8n.N8nChatHistoryMessage;
import com.group6.mvc.fpt_cinema.integration.n8n.N8nChatRequest;
import com.group6.mvc.fpt_cinema.integration.n8n.N8nChatResponse;
import com.group6.mvc.fpt_cinema.integration.n8n.N8nMovieContext;
import com.group6.mvc.fpt_cinema.repository.AiConversationRepository;
import com.group6.mvc.fpt_cinema.repository.AiMessageRepository;
import com.group6.mvc.fpt_cinema.repository.MovieRepository;
import com.group6.mvc.fpt_cinema.repository.UserRepository;
import com.group6.mvc.fpt_cinema.service.ChatService;

@Service
public class ChatServiceImpl implements ChatService {

    private static final int MAX_MESSAGE_LENGTH = 2000;
    private static final int MAX_CONTEXT_MOVIES = 50;
    private static final int MAX_HISTORY_MESSAGES = 20;
    private static final Set<String> SUPPORTED_INTENTS = Set.of(
            "MOVIE_LIST",
            "SHOWTIME_LIST",
            "PROMOTION_LIST",
            "PRODUCT_LIST",
            "BOOKING_LOOKUP",
            "TICKET_LOOKUP",
            "GENERAL_CHAT");

    private final AiConversationRepository conversationRepository;
    private final AiMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final N8nChatClient n8nChatClient;

    public ChatServiceImpl(
            AiConversationRepository conversationRepository,
            AiMessageRepository messageRepository,
            UserRepository userRepository,
            MovieRepository movieRepository,
            N8nChatClient n8nChatClient) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.n8nChatClient = n8nChatClient;
    }

    @Override
    @Transactional
    public ChatConversationResponse createConversation(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Ai_Conversation conversation = new Ai_Conversation();
        conversation.setUser(user);
        conversation.setChannel("WEB");
        conversation.setStatus("OPEN");

        return toConversationResponse(conversationRepository.saveAndFlush(conversation));
    }

    @Override
    @Transactional
    public ChatReplyResponse sendMessage(
            Integer userId,
            Integer conversationId,
            String message) {
        String normalizedMessage = normalizeMessage(message);
        Ai_Conversation conversation = getOwnedConversation(conversationId, userId);

        if (!"OPEN".equalsIgnoreCase(conversation.getStatus())) {
            throw new AppException(ErrorCode.CHAT_CONVERSATION_CLOSED);
        }

        List<N8nChatHistoryMessage> history = buildHistory(conversationId);

        saveMessage(conversation, "USER", normalizedMessage, null);

        N8nChatResponse n8nResponse = n8nChatClient.sendMessage(new N8nChatRequest(
                conversationId,
                userId,
                normalizedMessage,
                buildChatContext(history)));

        String intent = normalizeIntent(n8nResponse.intent());
        saveMessage(conversation, "BOT", n8nResponse.answer().trim(), intent);

        return ChatReplyResponse.builder()
                .conversationId(conversationId)
                .answer(n8nResponse.answer().trim())
                .intent(intent)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(
            Integer userId,
            Integer conversationId) {
        getOwnedConversation(conversationId, userId);
        return messageRepository
                .findAllByConversationIdOrderByCreatedAtAscIdAsc(conversationId)
                .stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @Override
    @Transactional
    public ChatConversationResponse closeConversation(
            Integer userId,
            Integer conversationId) {
        Ai_Conversation conversation = getOwnedConversation(conversationId, userId);
        if ("OPEN".equalsIgnoreCase(conversation.getStatus())) {
            conversation.setStatus("CLOSED");
            conversation.setEndedAt(LocalDateTime.now());
        }
        return toConversationResponse(conversationRepository.save(conversation));
    }

    private Ai_Conversation getOwnedConversation(
            Integer conversationId,
            Integer userId) {
        return conversationRepository.findByIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new AppException(
                        ErrorCode.CHAT_CONVERSATION_NOT_FOUND));
    }

    private Ai_Message saveMessage(
            Ai_Conversation conversation,
            String sender,
            String content,
            String intent) {
        Ai_Message message = new Ai_Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content);
        message.setIntentType(intent);
        return messageRepository.save(message);
    }

    private String normalizeMessage(String message) {
        if (message == null || message.isBlank()) {
            throw new AppException(ErrorCode.INVALID_CHAT_MESSAGE);
        }

        String normalizedMessage = message.trim();
        if (normalizedMessage.length() > MAX_MESSAGE_LENGTH) {
            throw new AppException(ErrorCode.INVALID_CHAT_MESSAGE);
        }
        return normalizedMessage;
    }

    private String normalizeIntent(String intent) {
        if (intent == null || intent.isBlank()) {
            return "GENERAL_CHAT";
        }

        String normalizedIntent = intent.trim().toUpperCase(Locale.ROOT);
        return SUPPORTED_INTENTS.contains(normalizedIntent)
                ? normalizedIntent
                : "GENERAL_CHAT";
    }

    private List<N8nChatHistoryMessage> buildHistory(Integer conversationId) {
        List<Ai_Message> messages = messageRepository
                .findAllByConversationIdOrderByCreatedAtAscIdAsc(conversationId);

        int fromIndex = Math.max(0, messages.size() - MAX_HISTORY_MESSAGES);
        return messages.subList(fromIndex, messages.size())
                .stream()
                .map(message -> new N8nChatHistoryMessage(
                        "BOT".equalsIgnoreCase(message.getSender())
                                ? "assistant"
                                : "user",
                        message.getContent()))
                .toList();
    }

    private N8nChatContext buildChatContext(List<N8nChatHistoryMessage> history) {
        List<N8nMovieContext> movies = movieRepository.findAll(PageRequest.of(
                        0,
                        MAX_CONTEXT_MOVIES,
                        Sort.by(Sort.Direction.DESC, "id")))
                .stream()
                .map(movie -> new N8nMovieContext(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getGenre(),
                        movie.getDurationMinutes(),
                        movie.getAgeRating(),
                        movie.getReleaseDate(),
                        movie.getDescription(),
                        movie.getStatus()))
                .toList();

        return new N8nChatContext(movies, history);
    }

    private ChatConversationResponse toConversationResponse(
            Ai_Conversation conversation) {
        return ChatConversationResponse.builder()
                .id(conversation.getId())
                .channel(conversation.getChannel())
                .status(conversation.getStatus())
                .startedAt(conversation.getStartedAt())
                .endedAt(conversation.getEndedAt())
                .build();
    }

    private ChatMessageResponse toMessageResponse(Ai_Message message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .sender(message.getSender())
                .content(message.getContent())
                .intent(message.getIntentType())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
