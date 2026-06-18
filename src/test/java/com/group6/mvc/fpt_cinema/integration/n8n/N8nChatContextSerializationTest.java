package com.group6.mvc.fpt_cinema.integration.n8n;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

import com.group6.mvc.fpt_cinema.enums.MovieGenre;
import com.group6.mvc.fpt_cinema.enums.MovieStatus;
import com.sun.net.httpserver.HttpServer;

class N8nChatContextSerializationTest {

        @Test
        void serializesMovieContext() throws IOException {
                AtomicReference<String> receivedBody = new AtomicReference<>();
                HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
                server.createContext("/webhook/chat-api", exchange -> {
                        receivedBody.set(new String(
                                        exchange.getRequestBody().readAllBytes(),
                                        StandardCharsets.UTF_8));

                        byte[] response = """
                                        {
                                          "answer": "AI response",
                                          "intent": "MOVIE_LIST"
                                        }
                                        """.getBytes(StandardCharsets.UTF_8);
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.length);
                        exchange.getResponseBody().write(response);
                        exchange.close();
                });
                server.start();

                try {
                        N8nChatClient client = new N8nChatClient(
                                        "http://localhost:" + server.getAddress().getPort()
                                                        + "/webhook/chat-api",
                                        "test-secret",
                                        "X-Webhook-Secret");

                        client.sendMessage(new N8nChatRequest(
                                        12,
                                        5,
                                        "List movies",
                                        new N8nChatContext(List.of(new N8nMovieContext(
                                                        7,
                                                        "Movie title",
                                                        MovieGenre.ACTION,
                                                        120,
                                                        "T13",
                                                        null,
                                                        "Movie description",
                                                        MovieStatus.NOW_SHOWING)))));

                        assertThat(receivedBody.get())
                                        .contains("\"context\":{\"movies\":[")
                                        .contains("\"title\":\"Movie title\"")
                                        .contains("\"status\":\"NOW_SHOWING\"");
                } finally {
                        server.stop(0);
                }
        }
}
