package com.airbnb_clone.pin.domain.dto.request;

import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "이미지 URL은 필수입니다.")
    private String imageUrl;
    @NotNull(message = "사용자 번호는 필수입니다.")
    private Long userNo;

    public static TemporaryPinCreateRequestDTO of(String imageUrl, Long userNo) {
        return TemporaryPinCreateRequestDTO.builder()
                .imageUrl(imageUrl)
                .userNo(userNo)
                .build();
    }
}
