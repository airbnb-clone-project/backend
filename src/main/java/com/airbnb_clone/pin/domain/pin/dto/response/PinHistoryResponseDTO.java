package com.airbnb_clone.pin.domain.pin.dto.response;

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
public class PinHistoryResponseDTO {
    private Long pinNo;
    private String imageClassification;

    private LocalDateTime createdAt;

    public static PinHistoryResponseDTO of(Long pinNo, String imageClassification, LocalDateTime createdAt) {
        return PinHistoryResponseDTO.builder()
                .pinNo(pinNo)
                .imageClassification(imageClassification)
                .createdAt(createdAt)
                .build();
    }
}
