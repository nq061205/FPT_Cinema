package com.group6.mvc.fpt_cinema.integration.n8n;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.sun.net.httpserver.HttpServer;

class N8nChatClientTest {

    private HttpServer server;
    private String webhookUrl;
    private final AtomicReference<String> receivedSecret = new AtomicReference<>();
    private final AtomicReference<String> receivedBody = new AtomicReference<>();

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/webhook/chat-api", exchange -> {
            receivedSecret.set(exchange.getRequestHeaders()
                    .getFirst("X-Webhook-Secret"));
            receivedBody.set(new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8));

            byte[] response = """
                    {
                      "answer": "AI response",
                      "intent": "GENERAL_CHAT"
                    }
                    """.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders()
                    .set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        webhookUrl = "http://localhost:" + server.getAddress().getPort()
                + "/webhook/chat-api";
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void sendsExpectedHeaderAndJsonToN8n() {
        N8nChatClient client = new N8nChatClient(
                " " + webhookUrl + " ",
                "test-secret\r\n",
                " X-Webhook-Secret ");

        N8nChatResponse response = client.sendMessage(
                new N8nChatRequest(12, 5, "Hôm nay có phim gì?"));

        assertThat(response.answer()).isEqualTo("AI response");
        assertThat(response.intent()).isEqualTo("GENERAL_CHAT");
        assertThat(receivedSecret.get()).isEqualTo("test-secret");
        assertThat(receivedBody.get())
                .contains("\"conversationId\":12")
                .contains("\"userId\":5")
                .contains("\"message\":\"Hôm nay có phim gì?\"");
    }
}
