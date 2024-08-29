package com.airbnb_clone.pin.domain.dto.request;

import lombok.*;

/**
 * packageName    : com.airbnb_clone.pin.repository
 * fileName       : TemporaryPinCreateRequestDTO
 * author         : ipeac
 * date           : 24. 8. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 29.        ipeac       최초 생성
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class TemporaryPinCreateRequestDTO {
    private String imageUrl;
    private Long userNo;

    public static TemporaryPinCreateRequestDTO of(String imageUrl, Long userNo) {
        return TemporaryPinCreateRequestDTO.builder()
                .imageUrl(imageUrl)
                .userNo(userNo)
                .build();
    }
}
