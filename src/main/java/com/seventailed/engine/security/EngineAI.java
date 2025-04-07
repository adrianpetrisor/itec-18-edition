package com.seventailed.engine.security;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class EngineAI {
    private WebClient AIClient = WebClient.builder().baseUrl("https://api.cohere.ai/v1").defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer UMpAosvjvLzNWxXKyR2UCWifXu8ibamVOPXyS1mP").defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

    @Bean
    public WebClient getAIClient () {
        return AIClient;
    }

    public Mono<String> getResponse(String prompt) {
        return AIClient.post()
                .uri("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "message", prompt + ". Very short response, for academic purposes. If the question is out of place, say something accordingly instead.")
                )
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.at("/text").asText())
                .onErrorResume(e -> {
                    return Mono.just("An error occurred: " + e.getMessage());
                });
    }
}
