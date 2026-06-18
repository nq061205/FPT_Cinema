package com.group6.mvc.fpt_cinema.integration.n8n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.group6.mvc.fpt_cinema.enums.ErrorCode;
import com.group6.mvc.fpt_cinema.exception.AppException;

@Component
public class N8nChatClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(N8nChatClient.class);

    private final RestClient restClient;
    private final String webhookUrl;
    private final String webhookSecret;
    private final String secretHeader;

    public N8nChatClient(
            @Value("${n8n.chat.webhook-url:}") String webhookUrl,
            @Value("${n8n.chat.webhook-secret:}") String webhookSecret,
            @Value("${n8n.chat.secret-header:X-Webhook-Secret}") String secretHeader) {
        this.restClient = RestClient.create();
        this.webhookUrl = normalizeConfigurationValue(webhookUrl);
        this.webhookSecret = normalizeConfigurationValue(webhookSecret);
        this.secretHeader = normalizeConfigurationValue(secretHeader);
    }

    public N8nChatResponse sendMessage(N8nChatRequest request) {
        if (webhookUrl.isBlank() || webhookSecret.isBlank()) {
            LOGGER.error(
                    "n8n chat configuration is missing: webhookUrlConfigured={}, webhookSecretConfigured={}",
                    !webhookUrl.isBlank(),
                    !webhookSecret.isBlank());
            throw new AppException(ErrorCode.CHAT_SERVICE_UNAVAILABLE);
        }

        try {
            N8nChatResponse response = restClient.post()
                    .uri(webhookUrl)
                    .header(secretHeader, webhookSecret)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(N8nChatResponse.class);

            if (response == null || response.answer() == null || response.answer().isBlank()) {
                LOGGER.error("n8n chat response is missing a non-blank 'answer' field");
                throw new AppException(ErrorCode.CHAT_SERVICE_UNAVAILABLE);
            }
            return response;
        } catch (AppException exception) {
            throw exception;
        } catch (IllegalArgumentException exception) {
            LOGGER.error("n8n chat configuration contains an invalid URL or header value");
            throw new AppException(ErrorCode.CHAT_SERVICE_UNAVAILABLE);
        } catch (RestClientResponseException exception) {
            LOGGER.error(
                    "n8n chat request failed with HTTP status {} for webhook {}",
                    exception.getStatusCode().value(),
                    webhookUrl);
            throw new AppException(ErrorCode.CHAT_SERVICE_UNAVAILABLE);
        } catch (RestClientException exception) {
            LOGGER.error(
                    "Could not call n8n chat webhook {}: {}",
                    webhookUrl,
                    exception.getMessage());
            throw new AppException(ErrorCode.CHAT_SERVICE_UNAVAILABLE);
        }
    }

    private static String normalizeConfigurationValue(String value) {
        return value == null ? "" : value.strip();
    }
}
