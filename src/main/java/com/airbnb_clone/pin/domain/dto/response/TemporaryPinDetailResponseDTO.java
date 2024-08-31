package com.airbnb_clone.pin.domain.dto.response;

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
public class TemporaryPinDetailResponseDTO {
    private String title;
    private String description;
    private String imgUrl;
    private String link;
    private boolean isCommentAllowed;
    private int boardNo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
