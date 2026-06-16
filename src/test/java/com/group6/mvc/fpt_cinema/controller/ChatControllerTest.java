package com.group6.mvc.fpt_cinema.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.group6.mvc.fpt_cinema.entity.Ai_Conversation;
import com.group6.mvc.fpt_cinema.entity.Ai_Message;
import com.group6.mvc.fpt_cinema.entity.Movie;
import com.group6.mvc.fpt_cinema.entity.Role;
import com.group6.mvc.fpt_cinema.entity.User;
import com.group6.mvc.fpt_cinema.integration.n8n.N8nChatClient;
import com.group6.mvc.fpt_cinema.integration.n8n.N8nChatRequest;
import com.group6.mvc.fpt_cinema.integration.n8n.N8nChatResponse;
import com.group6.mvc.fpt_cinema.repository.AiConversationRepository;
import com.group6.mvc.fpt_cinema.repository.AiMessageRepository;
import com.group6.mvc.fpt_cinema.repository.MovieRepository;
import com.group6.mvc.fpt_cinema.repository.RoleRepository;
import com.group6.mvc.fpt_cinema.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AiConversationRepository conversationRepository;

    @Autowired
    private AiMessageRepository messageRepository;

    @Autowired
    private MovieRepository movieRepository;

    @MockitoBean
    private N8nChatClient n8nChatClient;

    private User user;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        Role role = roleRepository.save(new Role(null, "chat-test-customer"));
        user = createUser(role, "chat.user@test.com", "0901111111");
        anotherUser = createUser(role, "another.chat.user@test.com", "0902222222");
    }

    @Test
    void authenticatedUserCanCreateConversationAndSendMessage() throws Exception {
        createMovie();

        mockMvc.perform(post("/api/chat/conversations")
                        .with(jwtFor(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.status").value("OPEN"))
                .andExpect(jsonPath("$.result.channel").value("WEB"));

        Ai_Conversation conversation = conversationRepository.findAll().get(0);
        when(n8nChatClient.sendMessage(any(N8nChatRequest.class)))
                .thenReturn(new N8nChatResponse(
                        "Hôm nay FPT Cinema có các phim đang chiếu.",
                        "MOVIE_LIST"));

        mockMvc.perform(post("/api/chat/conversations/{id}/messages",
                        conversation.getId())
                        .with(jwtFor(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "Hôm nay có phim gì?"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.conversationId")
                        .value(conversation.getId()))
                .andExpect(jsonPath("$.result.answer")
                        .value("Hôm nay FPT Cinema có các phim đang chiếu."))
                .andExpect(jsonPath("$.result.intent").value("MOVIE_LIST"));

        ArgumentCaptor<N8nChatRequest> requestCaptor =
                ArgumentCaptor.forClass(N8nChatRequest.class);
        verify(n8nChatClient).sendMessage(requestCaptor.capture());
        assertThat(requestCaptor.getValue().conversationId())
                .isEqualTo(conversation.getId());
        assertThat(requestCaptor.getValue().userId()).isEqualTo(user.getId());
        assertThat(requestCaptor.getValue().context().movies())
                .extracting(movieContext -> movieContext.title())
                .contains("Chat Test Movie");
        assertThat(requestCaptor.getValue().message())
                .isEqualTo("Hôm nay có phim gì?");

        List<Ai_Message> savedMessages = messageRepository
                .findAllByConversationIdOrderByCreatedAtAscIdAsc(conversation.getId());
        assertThat(savedMessages)
                .extracting(Ai_Message::getSender)
                .containsExactly("USER", "BOT");
        assertThat(savedMessages.get(1).getIntentType()).isEqualTo("MOVIE_LIST");
    }

    @Test
    void userCanReadHistoryAndCloseOwnConversation() throws Exception {
        Ai_Conversation conversation = createConversation(user);
        createMessage(conversation, "USER", "Xin chào");
        createMessage(conversation, "BOT", "Xin chào bạn");

        mockMvc.perform(get("/api/chat/conversations/{id}/messages",
                        conversation.getId())
                        .with(jwtFor(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].sender").value("USER"))
                .andExpect(jsonPath("$.result[1].sender").value("BOT"));

        mockMvc.perform(put("/api/chat/conversations/{id}/close",
                        conversation.getId())
                        .with(jwtFor(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.status").value("CLOSED"))
                .andExpect(jsonPath("$.result.endedAt").exists());
    }

    @Test
    void userCannotAccessAnotherUsersConversation() throws Exception {
        Ai_Conversation conversation = createConversation(anotherUser);

        mockMvc.perform(get("/api/chat/conversations/{id}/messages",
                        conversation.getId())
                        .with(jwtFor(user)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(3001));
    }

    @Test
    void closedConversationRejectsNewMessages() throws Exception {
        Ai_Conversation conversation = createConversation(user);
        conversation.setStatus("CLOSED");
        conversationRepository.save(conversation);

        mockMvc.perform(post("/api/chat/conversations/{id}/messages",
                        conversation.getId())
                        .with(jwtFor(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "Tin nhắn mới"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value(3002));

        verify(n8nChatClient, never()).sendMessage(any());
    }

    @Test
    void chatRequiresAuthentication() throws Exception {
        mockMvc.perform(post("/api/chat/conversations"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(1007));
    }

    private User createUser(Role role, String email, String phone) {
        User newUser = new User();
        newUser.setRole(role);
        newUser.setFullName("Chat Test User");
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setPasswordHash("$2a$10$testHash");
        return userRepository.save(newUser);
    }

    private void createMovie() {
        Movie movie = new Movie();
        movie.setTitle("Chat Test Movie");
        movie.setGenre("ACTION");
        movie.setDurationMinutes(120);
        movie.setAgeRating("T13");
        movie.setDescription("Movie data supplied to n8n");
        movie.setStatus("NOW_SHOWING");
        movieRepository.save(movie);
    }

    private Ai_Conversation createConversation(User owner) {
        Ai_Conversation conversation = new Ai_Conversation();
        conversation.setUser(owner);
        return conversationRepository.save(conversation);
    }

    private void createMessage(
            Ai_Conversation conversation,
            String sender,
            String content) {
        Ai_Message message = new Ai_Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content);
        messageRepository.save(message);
    }

    private org.springframework.test.web.servlet.request.RequestPostProcessor jwtFor(
            User authenticatedUser) {
        return jwt().jwt(jwt -> jwt.claim("userId", authenticatedUser.getId()));
    }
}
