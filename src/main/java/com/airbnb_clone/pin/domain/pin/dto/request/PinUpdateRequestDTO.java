package com.airbnb_clone.pin.domain.pin.dto.request;

import lombok.*;

/**
 * packageName    : com.airbnb_clone.pin.controller
 * fileName       : PinUpdateRequestDTO
 * author         : ipeac
 * date           : 24. 9. 9.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 9.        ipeac       최초 생성
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class PinUpdateRequestDTO {
    private String title;
    private String description;
    private String link;
    private Long boardNo;
    private boolean isCommentAllowed;

    public static PinUpdateRequestDTO of(String title, String description, String link, Long boardNo, boolean isCommentAllowed) {
        return PinUpdateRequestDTO.builder()
                .title(title)
                .description(description)
                .link(link)
                .boardNo(boardNo)
                .isCommentAllowed(isCommentAllowed)
                .build();
    }
}
