package com.airbnb_clone.pin.repository;

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
    private String fileUrl;
    private Long userNo;

    public static TemporaryPinCreateRequestDTO of(String fileUrl, Long userNo) {
        return TemporaryPinCreateRequestDTO.builder()
                .fileUrl(fileUrl)
                .userNo(userNo)
                .build();
    }
}
