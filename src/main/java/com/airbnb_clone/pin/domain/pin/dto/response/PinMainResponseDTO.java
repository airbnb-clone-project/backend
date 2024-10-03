package com.airbnb_clone.pin.domain.pin.dto.response;

import com.airbnb_clone.pin.domain.pin.redis.MainPinHash;
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
public class PinMainResponseDTO {
    private Long pinNo;
    private String imageUrl;
    private String link;
    private Long userNo;
    private String imageClassification;
    private LocalDateTime createdAt;
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
