package com.airbnb_clone.pin.domain.pin.dto.response;

import com.airbnb_clone.pin.domain.pin.redis.MainPinHash;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

/**
 * packageName    : com.airbnb_clone.pin.repository
 * fileName       : PinMainResponseDTO
 * author         : ipeac
 * date           : 24. 9. 10.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 10.        ipeac       최초 생성
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Schema(name = "핀 메인 응답 DTO", description = "핀 메인 응답 DTO")
public class PinMainResponseDTO {
    @Schema(description = "핀 번호", example = "1")
    private Long pinNo;

    @Schema(description = "이미지 URL", example = "http://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "링크", example = "http://example.com")
    private String link;

    @Schema(description = "사용자 번호", example = "1")
    private Long userNo;

    @Schema(description = "이미지 분류", example = "인테리어")
    private String imageClassification;

    @Schema(description = "생성 일시", example = "2023-08-30T12:34:56")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시", example = "2023-08-30T12:34:56")
    private LocalDateTime updatedAt;

    public static PinMainResponseDTO of(long pinNo, String imageUrl, String link, String imageClassification, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return PinMainResponseDTO.builder()
                .pinNo(pinNo)
                .imageUrl(imageUrl)
                .link(link)
                .imageClassification(imageClassification)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public MainPinHash toMainPinHash() {
        return MainPinHash.of(String.valueOf(pinNo), imageUrl, link, userNo, imageClassification, createdAt, updatedAt);
    }
}
