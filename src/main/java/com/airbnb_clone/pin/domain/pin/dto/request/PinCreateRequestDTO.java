package com.airbnb_clone.pin.domain.pin.dto.request;

import com.airbnb_clone.image.enums.ImageClassificationEnum;
import com.airbnb_clone.pin.domain.pin.Pin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * DTO for {@link Pin}
 */
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder(access = lombok.AccessLevel.PRIVATE)
public class PinCreateRequestDTO {

    @NotNull(message = "사용자 번호는 필수입니다")
    @Positive(message = "사용자 번호는 0보다 커야합니다")
    private Long userNo;

    @NotNull(message = "이미지는 필수입니다")
    @Pattern(regexp = "^(http|https)://.*$", message = "이미지 URL 형식이 올바르지 않습니다")
    private String imgUrl;

    @NotNull(message = "이미지 분류값은 필수입니다")
    private String imageClassification;

    private String title;

    private String description;

    private String link;

    private Long boardNo;

    private Set<Long> tagNos = new HashSet<>();

    private boolean isCommentAllowed;

    public static PinCreateRequestDTO of(@NotNull Long userNo, @NotNull String imgUrl, String title, String description, String link, Long boardNo, boolean isCommentAllowed, Set<Long> tagNos, ImageClassificationEnum imageClassification) {
        return PinCreateRequestDTO.builder()
                .userNo(userNo)
                .imgUrl(imgUrl)
                .title(title)
                .description(description)
                .link(link)
                .boardNo(boardNo)
                .imageClassification(imageClassification.getKoreanName())
                .isCommentAllowed(isCommentAllowed)
                .tagNos(tagNos)
                .build();
    }

    public Pin toVO() {
        return Pin.builder()
                .userNo(userNo)
                .imgUrl(imgUrl)
                .title(title)
                .description(description)
                .link(link)
                .boardNo(boardNo)
                .isCommentAllowed(isCommentAllowed)
                .imageClassification(imageClassification)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
