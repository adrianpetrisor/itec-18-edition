package com.seventailed.engine.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;

@Component
public class EngineMetaDefender {
    private final WebClient webClient;

    public EngineMetaDefender(@Value("${metadefender.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.metadefender.com/v4")
                .defaultHeader("apikey", apiKey)
                .build();
    }
    public Mono<String> scanFile(MultipartFile file) {
        ByteArrayResource resource = new ByteArrayResource(getBytes(file)) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("file", resource);

        return webClient.post()
                .uri("/file")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("data_id"))
                .onErrorResume(error -> {
                    error.printStackTrace();
                    return Mono.just("ERROR: " + error.getMessage());
                });
    }

    public Mono<Map> getScanResults(String dataId) {
        return webClient.get()
                .uri("/file/" + dataId)
                .retrieve()
                .bodyToMono(Map.class);
    }

    public Mono<Boolean> isFileSafe(String dataId) {
        return webClient.get()
                .uri("/file/" + dataId)
                .retrieve()
                .bodyToMono(Map.class)
                .map(result -> {
                    Map<String, Object> scanResults = (Map<String, Object>) result.get("scan_results");
                    Map<String, Object> scanDetails = (Map<String, Object>) scanResults.get("scan_details");

                    for (Object engine : scanDetails.values()) {
                        Map<String, Object> engineResult = (Map<String, Object>) engine;
                        Integer resultCode = (Integer) engineResult.get("scan_result_i");
                        if (resultCode != null && resultCode != 0) {
                            return false;
                        }
                    }
                    return true;
                })
                .onErrorResume(err -> {
                    err.printStackTrace();
                    return Mono.just(false);
                });
    }

    private byte[] getBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }
}
