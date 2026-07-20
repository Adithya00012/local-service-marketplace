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
import java.util.ArrayList;
import java.util.List;

@Component
public class EmbeddingService {

    @Value("${cohere.api.key}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Double> generateEmbedding(String text) {
        try {
            String url = "https://api.cohere.com/v1/embed";

            String requestBody = objectMapper.writeValueAsString(
                    java.util.Map.of(
                            "texts", java.util.List.of(text),
                            "model", "embed-english-v3.0",
                            "input_type", "search_document"));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(20))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Cohere API error: " + response.body());
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode embeddingArray = root.path("embeddings").get(0);

            List<Double> embedding = new ArrayList<>();
            for (JsonNode value : embeddingArray) {
                embedding.add(value.asDouble());
            }
            return embedding;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate embedding: " + e.getMessage(), e);
        }
    }

    // Converts a List<Double> to a comma-separated string for DB storage
    public String embeddingToString(List<Double> embedding) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < embedding.size(); i++) {
            if (i > 0)
                sb.append(",");
            sb.append(embedding.get(i));
        }
        return sb.toString();
    }

    // Converts the stored comma-separated string back into numbers
    public List<Double> stringToEmbedding(String str) {
        List<Double> result = new ArrayList<>();
        if (str == null || str.isBlank())
            return result;
        for (String part : str.split(",")) {
            result.add(Double.parseDouble(part));
        }
        return result;
    }

    // Cosine similarity: measures how "close" two vectors point, from -1 to 1 (1 =
    // identical meaning)
    public double cosineSimilarity(List<Double> a, List<Double> b) {
        double dotProduct = 0.0, normA = 0.0, normB = 0.0;
        for (int i = 0; i < a.size(); i++) {
            dotProduct += a.get(i) * b.get(i);
            normA += Math.pow(a.get(i), 2);
            normB += Math.pow(b.get(i), 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}