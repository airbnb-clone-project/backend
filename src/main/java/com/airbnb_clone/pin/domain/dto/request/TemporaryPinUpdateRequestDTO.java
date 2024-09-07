package com.airbnb_clone.pin.domain.dto.request;

import com.airbnb_clone.pin.domain.InnerTempPin;
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
public final class TemporaryPinUpdateRequestDTO {
    private Integer boardNo;

    private String description;

    private String title;

    private boolean isCommentAllowed;

    @Pattern(message = "올바른 URL 양식이 아닙니다.", regexp = "^(https?://)?([\\da-z.-]+)\\.([a-z.]{2,6})([/\\w .-]*)*/?$")
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
