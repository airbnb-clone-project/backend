package com.airbnb_clone.pin.domain.pin.dto.request;

import com.airbnb_clone.pin.domain.pin.InnerTempPin;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.*;

/**
 * DTO for {@link InnerTempPin}
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
@Schema(name = "임시핀 수정 요청 DTO", description = "임시핀 수정 요청 DTO")
public final class TemporaryPinUpdateRequestDTO {
    @Schema(description = "보드 번호", example = "123")
    private Integer boardNo;

    @Schema(description = "핀 설명", example = "A beautiful sunset over the mountains.")
    private String description;

    @Schema(description = "핀 제목", example = "Beautiful Sunset")
    private String title;

    @Schema(description = "댓글 허용 여부", example = "false", defaultValue = "false")
    private boolean isCommentAllowed;

    @Pattern(message = "올바른 URL 양식이 아닙니다.", regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$")
    @Schema(description = "링크", example = "http://example.com")
    private String link;

    public static TemporaryPinUpdateRequestDTO of(int boardNo, String description, String title, boolean isCommentAllowed, String link) {
        return TemporaryPinUpdateRequestDTO.builder()
                .boardNo(boardNo)
                .description(description)
                .title(title)
                .isCommentAllowed(isCommentAllowed)
                .link(link)
                .build();
    }
}
