package com.airbnb_clone.pin.domain.pin.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

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
    @NotNull(message = "이미지 파일은 필수입니다.")
    private MultipartFile imageFile;

    public static TemporaryPinCreateRequestDTO of(MultipartFile imageFile) {
        return TemporaryPinCreateRequestDTO.builder()
                .imageFile(imageFile)
                .build();
    }
}
