package com.airbnb_clone.pin.domain.pin.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * packageName    : com.airbnb_clone.pin.controller
 * fileName       : TemporaryPinDetailResponseDTO
 * author         : ipeac
 * date           : 24. 8. 30.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 30.        ipeac       최초 생성
 */
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TemporaryPinsResponseDTO {
    private String tempPinNo;
    private String title;
    private String description;
    private String imgUrl;
    private String link;
    private boolean isCommentAllowed;
    private Integer boardNo;
    private String imageClassification;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
