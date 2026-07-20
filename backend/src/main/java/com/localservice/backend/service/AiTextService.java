package com.localservice.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class AiTextService {

        @Value("${groq.api.key}")
        private String apiKey;

        private final HttpClient httpClient = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build();

        private final ObjectMapper objectMapper = new ObjectMapper();

        public String generateText(String prompt) {
                try {
                        String url = "https://api.groq.com/openai/v1/chat/completions";

                        String requestBody = objectMapper.writeValueAsString(
                                        java.util.Map.of(
                                                        "model", "llama-3.3-70b-versatile",
                                                        "messages", java.util.List.of(
                                                                        java.util.Map.of("role", "user", "content",
                                                                                        prompt))));

                        HttpRequest request = HttpRequest.newBuilder()
                                        .uri(URI.create(url))
                                        .header("Content-Type", "application/json")
                                        .header("Authorization", "Bearer " + apiKey)
                                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                                        .timeout(Duration.ofSeconds(20))
                                        .build();

                        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                        if (response.statusCode() != 200) {
                                throw new RuntimeException("Groq API error: " + response.body());
                        }

                        JsonNode root = objectMapper.readTree(response.body());
                        return root.path("choices").get(0)
                                        .path("message").path("content").asText();

                } catch (Exception e) {
                        throw new RuntimeException("Failed to generate AI content: " + e.getMessage(), e);
                }
        }
}