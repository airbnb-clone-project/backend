package com.airbnb_clone.image.service;

import com.airbnb_clone.image.enums.ImageClassificationEnum;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.Objects;

/**
 * packageName    : com.airbnb_clone.image.service
 * fileName       : ExternalApiService
 * author         : ipeac
 * date           : 24. 9. 22.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 22.        ipeac       최초 생성
 */
@Service
@Slf4j
public class ExternalApiService {
    public static final String IMAGE_CLASSIFY_API_BASE_URL = "http://39.115.84.63";

    private WebClient webClient;

    @PostConstruct
    public void init() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(5000));

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(IMAGE_CLASSIFY_API_BASE_URL)
                .build();
    }

    public Mono<ImageClassificationEnum> classifyImage(MultipartFile imageFile) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("imageFile", imageFile.getResource())
                .filename(Objects.requireNonNull(imageFile.getOriginalFilename()));

        return webClient.post()
                .uri("/api/image/classify")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(String.class)
                .map(ImageClassificationEnum::fromKoreanName)
                .onErrorResume(WebClientResponseException.class, e -> Mono.error(new RuntimeException(MessageFormat.format("maybe Invalid image file \n{0}", e.getMessage()))))
                .onErrorResume(Exception.class, e -> Mono.error(new RuntimeException(MessageFormat.format("Unexpected error occurred: {0}", e.getMessage()))));
    }
}
