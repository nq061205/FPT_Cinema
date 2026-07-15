package com.group6.mvc.fpt_cinema.integration.gemini;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.enums.MovieGenre;
import com.group6.mvc.fpt_cinema.enums.MovieStatus;
import com.group6.mvc.fpt_cinema.exception.AppException;
import com.sun.net.httpserver.HttpServer;

class GeminiChatClientTest {

    private static final String VALID_RESPONSE = """
            {
              "candidates": [{
                "finishReason": "STOP",
                "content": {
                  "parts": [{
                    "text": "{\\"answer\\":\\"AI response\\",\\"intent\\":\\"MOVIE_LIST\\"}"
                  }]
                }
              }]
            }
            """;

    private static final String TRUNCATED_RESPONSE = """
            {
              "candidates": [{
                "finishReason": "STOP",
                "content": {
                  "parts": [{
                    "text": "{\\"answer\\":\\"Incomplete\\",\\"intent\\":\\"GENERAL_CHAT\\""
                  }]
                }
              }]
            }
            """;

    private static final String FENCED_RESPONSE_WITH_THOUGHT = """
            {
              "candidates": [{
                "finishReason": "STOP",
                "content": {
                  "parts": [
                    {"thought": true, "text": "This is not part of the answer."},
                    {"text": "```json\\n{\\"answer\\":\\"Fenced response\\",\\"intent\\":\\"GENERAL_CHAT\\"}\\n```"}
                  ]
                }
              }]
            }
            """;

    private HttpServer server;
    private String apiBaseUrl;
    private final AtomicReference<String> receivedApiKey = new AtomicReference<>();
    private final AtomicReference<String> receivedBody = new AtomicReference<>();
    private final AtomicInteger responseStatus = new AtomicInteger(200);
    private final AtomicInteger requestCount = new AtomicInteger();
    private final Queue<String> responsePayloads = new ConcurrentLinkedQueue<>();

    @BeforeEach
    void setUp() throws IOException {
        requestCount.set(0);
        responseStatus.set(200);
        responsePayloads.clear();
        responsePayloads.add(VALID_RESPONSE);

        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/v1beta/models/gemini-test:generateContent", exchange -> {
            requestCount.incrementAndGet();
            receivedApiKey.set(exchange.getRequestHeaders().getFirst("x-goog-api-key"));
            receivedBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));

            String payload = responsePayloads.poll();
            byte[] response = responseStatus.get() == 200
                    ? (payload == null ? VALID_RESPONSE : payload).getBytes(StandardCharsets.UTF_8)
                    : """
                            {
                              "error": {
                                "code": 429,
                                "status": "RESOURCE_EXHAUSTED"
                              }
                            }
                            """.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(responseStatus.get(), response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        apiBaseUrl = "http://localhost:" + server.getAddress().getPort() + "/v1beta";
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void sendsContextToGeminiAndParsesJsonReply() {
        GeminiChatClient client = new GeminiChatClient(" test-key ", "gemini-test", apiBaseUrl);

        GeminiChatResponse response = client.sendMessage(new GeminiChatRequest(
                "What movies are showing today?",
                new GeminiChatContext(
                        List.of(new GeminiMovieContext(
                                7, "Movie title", MovieGenre.ACTION, 120, "T13", null,
                                "Movie description", MovieStatus.NOW_SHOWING)),
                        List.of(new GeminiChatHistoryMessage("assistant", "Xin chào")))));

        assertThat(response.answer()).isEqualTo("AI response");
        assertThat(response.intent()).isEqualTo("MOVIE_LIST");
        assertThat(receivedApiKey.get()).isEqualTo("test-key");
        assertThat(receivedBody.get())
                .contains("\"system_instruction\"")
                .contains("\\\"title\\\":\\\"Movie title\\\"")
                .contains("\"role\":\"model\"")
                .contains("\"text\":\"What movies are showing today?\"")
                .contains("\"responseMimeType\":\"application/json\"")
                .doesNotContain("\"responseSchema\"");
    }

    @Test
    void sendsResponseSchemaWhenStructuredOutputIsEnabled() {
        GeminiChatClient client = new GeminiChatClient(
                "test-key", "gemini-test", apiBaseUrl, true);

        client.sendMessage(new GeminiChatRequest(
                "Hello",
                new GeminiChatContext(List.of(), List.of())));

        assertThat(receivedBody.get())
                .contains("\"responseSchema\"")
                .contains("\"required\":[\"answer\",\"intent\"]")
                .contains("MOVIE_LIST")
                .contains("GENERAL_CHAT");
    }

    @Test
    void ignoresThoughtPartsAndAcceptsMarkdownFencedJson() {
        responsePayloads.clear();
        responsePayloads.add(FENCED_RESPONSE_WITH_THOUGHT);
        GeminiChatClient client = new GeminiChatClient("test-key", "gemini-test", apiBaseUrl);

        GeminiChatResponse response = client.sendMessage(new GeminiChatRequest(
                "Hello",
                new GeminiChatContext(List.of(), List.of())));

        assertThat(response.answer()).isEqualTo("Fenced response");
        assertThat(response.intent()).isEqualTo("GENERAL_CHAT");
        assertThat(requestCount.get()).isEqualTo(1);
    }

    @Test
    void retriesOnceWhenGeminiReturnsTruncatedJson() {
        responsePayloads.clear();
        responsePayloads.add(TRUNCATED_RESPONSE);
        responsePayloads.add(VALID_RESPONSE);
        GeminiChatClient client = new GeminiChatClient("test-key", "gemini-test", apiBaseUrl);

        GeminiChatResponse response = client.sendMessage(new GeminiChatRequest(
                "Hello",
                new GeminiChatContext(List.of(), List.of())));

        assertThat(response.answer()).isEqualTo("AI response");
        assertThat(requestCount.get()).isEqualTo(2);
    }

    @Test
    void rejectsTruncatedJsonAfterTheSingleRetry() {
        responsePayloads.clear();
        responsePayloads.add(TRUNCATED_RESPONSE);
        responsePayloads.add(TRUNCATED_RESPONSE);
        GeminiChatClient client = new GeminiChatClient("test-key", "gemini-test", apiBaseUrl);

        assertThatThrownBy(() -> client.sendMessage(new GeminiChatRequest(
                "Hello",
                new GeminiChatContext(List.of(), List.of()))))
                .isInstanceOf(AppException.class)
                .extracting(exception -> ((AppException) exception).getErrorCode())
                .isEqualTo(ErrorCode.CHAT_SERVICE_UNAVAILABLE);
        assertThat(requestCount.get()).isEqualTo(2);
    }

    @Test
    void mapsGeminiRateLimitToAUsefulApplicationError() {
        responseStatus.set(429);
        GeminiChatClient client = new GeminiChatClient("test-key", "gemini-test", apiBaseUrl);

        assertThatThrownBy(() -> client.sendMessage(new GeminiChatRequest(
                "Hello",
                new GeminiChatContext(List.of(), List.of()))))
                .isInstanceOf(AppException.class)
                .extracting(exception -> ((AppException) exception).getErrorCode())
                .isEqualTo(ErrorCode.CHAT_SERVICE_RATE_LIMITED);
        assertThat(requestCount.get()).isEqualTo(1);
    }

    @Test
    void retriesServerErrorsOnceBeforeReturningUnavailable() {
        responseStatus.set(503);
        GeminiChatClient client = new GeminiChatClient("test-key", "gemini-test", apiBaseUrl);

        assertThatThrownBy(() -> client.sendMessage(new GeminiChatRequest(
                "Hello",
                new GeminiChatContext(List.of(), List.of()))))
                .isInstanceOf(AppException.class)
                .extracting(exception -> ((AppException) exception).getErrorCode())
                .isEqualTo(ErrorCode.CHAT_SERVICE_UNAVAILABLE);
        assertThat(requestCount.get()).isEqualTo(2);
    }
}
