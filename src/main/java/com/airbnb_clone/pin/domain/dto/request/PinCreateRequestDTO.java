package com.airbnb_clone.pin.domain.dto.request;

import com.airbnb_clone.pin.domain.Pin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

/**
 * DTO for {@link com.airbnb_clone.pin.domain.Pin}
 */
@Getter
@Setter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Builder(access = lombok.AccessLevel.PRIVATE)
public class PinCreateRequestDTO {

    @NotNull(message = "사용자 번호는 필수입니다")
    @Positive(message = "사용자 번호는 0보다 커야합니다")
    Long userNo;

    @NotNull(message = "이미지는 필수입니다")
    @URL(message = "URL 형식이여야 합니다.")
    String imgUrl;


    String title;

    String description;

    String link;

    Long boardNo;

    boolean isCommentAllowed;

    LocalDateTime createdAt;

    public static PinCreateRequestDTO of(@NotNull String imgUrl, String title, String description, String link, Long boardNo, boolean isCommentAllowed, LocalDateTime createdAt) {
        return PinCreateRequestDTO.builder()
                .imgUrl(imgUrl)
                .title(title)
                .description(description)
                .link(link)
                .boardNo(boardNo)
                .isCommentAllowed(isCommentAllowed)
                .createdAt(LocalDateTime.now())
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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
