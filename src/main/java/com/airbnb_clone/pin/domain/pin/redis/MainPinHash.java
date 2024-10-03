package com.airbnb_clone.pin.domain.pin.redis;

import com.airbnb_clone.pin.domain.pin.dto.response.PinMainResponseDTO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * packageName    : com.airbnb_clone.pin.domain.pin.redis
 * fileName       : MainPinHash
 * author         : ipeac
 * date           : 24. 9. 10.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 10.        ipeac       최초 생성
 */
@RedisHash("mainPin")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class MainPinHash {
    @Id
    private String pinNo;
    private String imageUrl;
    private String link;
    private Long userNo;

    @Indexed
    private String imageClassification;
    private Long createdAt;
    private Long updatedAt;

    public static MainPinHash of(String pinNo, String imageUrl, String link, Long userNo, String imageClassification, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return MainPinHash.builder()
                .pinNo(pinNo)
                .imageUrl(imageUrl)
                .link(link)
                .userNo(userNo)
                .imageClassification(imageClassification)
                .createdAt(createdAt.toInstant(ZoneOffset.UTC).toEpochMilli())
                .updatedAt(updatedAt.toInstant(ZoneOffset.UTC).toEpochMilli())
                .build();
    }

    //에포크 밀리초 시간을 LocalDateTime으로 변환 - start
    public LocalDateTime getCreatedAt() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneOffset.UTC);
    }

    public LocalDateTime getUpdatedAt() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(updatedAt), ZoneOffset.UTC);
    }
    //에포크 밀리초 시간을 LocalDateTime으로 변환 - end

    public static PinMainResponseDTO toPinMainResponseDTO(MainPinHash mainPinHash) {
        return PinMainResponseDTO.of(Long.parseLong(mainPinHash.getPinNo()), mainPinHash.getImageUrl(), mainPinHash.getLink(), mainPinHash.getImageClassification(), mainPinHash.getCreatedAt(), mainPinHash.getUpdatedAt());
    }
}
