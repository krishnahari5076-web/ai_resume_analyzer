package com.airesumeanalyzer.service;

import com.airesumeanalyzer.dto.AnalysisResult;
import com.airesumeanalyzer.exception.FileProcessingException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GroqService {
    private static final String CHAT_COMPLETIONS_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final int MAX_REQUEST_ATTEMPTS = 3;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();

    @Value("${groq.api.key}") private String apiKey;
    @Value("${groq.api.model}") private String model;
    @Value("${groq.api.max-output-tokens}") private int maxOutputTokens;
    @Value("${groq.api.temperature}") private double temperature;

    public GroqService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AnalysisResult analyzeResume(String resumeText) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new FileProcessingException("GROQ_API_KEY is not configured on the server.");
        }

        try {
            Map<String, Object> requestBody = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", "You are an expert technical recruiter and ATS specialist. Return strictly valid JSON only."),
                            Map.of("role", "user", "content", buildPrompt(resumeText))
                    ),
                    "temperature", temperature,
                    "max_completion_tokens", maxOutputTokens,
                    "response_format", Map.of("type", "json_object")
            );

            HttpRequest request = HttpRequest.newBuilder(URI.create(CHAT_COMPLETIONS_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(90))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestBody)))
                    .build();

            HttpResponse<String> response = sendWithRetry(request);
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.error("Groq request failed with status {}: {}", response.statusCode(), response.body());
                throw new FileProcessingException(errorMessageFor(response.statusCode()));
            }

            JsonNode root = objectMapper.readTree(response.body());
            String content = root.path("choices").path(0).path("message").path("content").asText();
            if (content.isBlank()) {
                throw new FileProcessingException("Groq returned an empty response.");
            }

            try {
                return objectMapper.readValue(content, AnalysisResult.class);
            } catch (JsonProcessingException e) {
                log.warn("Groq returned malformed JSON.", e);
                throw new FileProcessingException("Groq returned an incomplete analysis. Please try again.");
            }
        } catch (FileProcessingException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FileProcessingException("The Groq analysis request was interrupted. Please try again.", e);
        } catch (Exception e) {
            log.error("Groq API call failed", e);
            throw new FileProcessingException("Failed to analyse the resume using Groq.", e);
        }
    }

    private HttpResponse<String> sendWithRetry(HttpRequest request) throws Exception {
        for (int attempt = 1; attempt <= MAX_REQUEST_ATTEMPTS; attempt++) {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (!isRetryableStatus(response.statusCode()) || attempt == MAX_REQUEST_ATTEMPTS) {
                return response;
            }

            long delayMillis = 1_000L << (attempt - 1);
            log.warn("Groq is temporarily unavailable (HTTP {}). Retrying in {} seconds (attempt {}/{}).",
                    response.statusCode(), delayMillis / 1_000, attempt + 1, MAX_REQUEST_ATTEMPTS);
            Thread.sleep(delayMillis);
        }
        throw new IllegalStateException("Groq retry loop ended unexpectedly.");
    }

    private boolean isRetryableStatus(int statusCode) {
        return statusCode == 429 || (statusCode >= 500 && statusCode < 600);
    }

    private String errorMessageFor(int statusCode) {
        if (statusCode == 401) {
            return "Groq rejected the API key. Check GROQ_API_KEY in the IntelliJ Run Configuration.";
        }
        if (statusCode == 429) {
            return "Groq request limit reached. Please wait a minute and try again.";
        }
        if (statusCode == 503) {
            return "Groq is temporarily busy. Please try again in a few minutes.";
        }
        return "Groq could not analyse this resume. Please try again.";
    }

    private String buildPrompt(String resumeText) {
        return """
                Analyse the following resume as an ATS and hiring manager. Return only one valid JSON object.
                Use exactly these fields: atsScore (integer 0-100), summary (maximum 50 words), strengths,
                weaknesses, missingSkills, suggestions, and keywords. Each array must contain exactly three
                short items, with at most 10 words per item. Do not add Markdown, explanations, or extra fields.

                Resume text:
                ---
                %s
                ---
                """.formatted(resumeText);
    }
}
