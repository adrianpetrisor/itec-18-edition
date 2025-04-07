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
import java.util.Map;

@Component
public class EngineUploadcare {
    private WebClient webClient = WebClient.builder()
            .baseUrl("")
                .defaultHeader("Accept", "application/json")
                .build();

    @Value("${uploadcare.api.key}")
    private String publicKey;


    public Mono<String> uploadFile(MultipartFile file) {
        try {
            ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("UPLOADCARE_PUB_KEY", publicKey);
            body.add("UPLOADCARE_STORE", "1"); // auto-store file
            body.add("file", resource);

            return webClient.post()
                    .uri("/base/")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(body))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .map(json -> (String) json.get("file")) // UUID of uploaded file
                    .map(uuid -> "https://ucarecdn.com/" + uuid + "/"); // full CDN URL
        }catch (Exception exception) {
            exception.printStackTrace();
            return Mono.error(exception);
        }
    }
}
