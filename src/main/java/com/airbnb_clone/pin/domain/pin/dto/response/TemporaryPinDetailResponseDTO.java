package com.airbnb_clone.pin.domain.pin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "임시핀 상세 조회 응답 DTO", description = "임시핀 상세 조회 응답 DTO")
public class TemporaryPinDetailResponseDTO {
    @Schema(description = "핀 제목", example = "Beautiful Sunset")
    private String title;

    @Schema(description = "핀 설명", example = "A beautiful sunset over the mountains.")
    private String description;

    @Schema(description = "이미지 URL", example = "http://example.com/image.jpg")
    private String imgUrl;

    @Schema(description = "링크", example = "http://example.com")
    private String link;

    @Schema(description = "댓글 허용 여부", example = "true", defaultValue = "false")
    private boolean isCommentAllowed;

    @Schema(description = "게시판 번호", example = "123")
    private Integer boardNo;

    @Schema(description = "이미지 분류", example = "Nature")
    private String imageClassification;

    @Schema(description = "생성 일시", example = "2023-08-30T12:34:56")
    private LocalDateTime createdAt;

    @Schema(description = "수정 일시", example = "2023-08-30T12:34:56")
    private LocalDateTime updatedAt;
}
