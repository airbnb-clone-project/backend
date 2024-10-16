package com.airbnb_clone.pin.domain.pin;

import com.airbnb_clone.Tag;
import com.airbnb_clone.common.BaseTime;
import com.airbnb_clone.image.enums.ImageClassificationEnum;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Pin extends BaseTime {
    private Long no;
    private Long userNo;
    private String imgUrl;
    private String title;
    private String description;
    private String link;
    private Set<Tag> tags = new LinkedHashSet<>();
    private Long boardNo;
    private boolean isCommentAllowed;
    private String imageClassification;
    private int likeCount;

    private boolean isPinDeleted;

    public static Pin of(Long userNo, String imgUrl, String title, String description, String link, Set<Tag> tags, Long boardNo, boolean isCommentAllowed, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isPinDeleted, ImageClassificationEnum imageClassification) {
        return Pin.builder()
                .userNo(userNo)
                .imgUrl(imgUrl)
                .title(title)
                .description(description)
                .link(link)
                .tags(tags)
                .boardNo(boardNo)
                .isCommentAllowed(isCommentAllowed)
                .likeCount(0)
                .imageClassification(imageClassification.getKoreanName())
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .isPinDeleted(isPinDeleted)
                .build();
    }

    public boolean isOwner(Long usersNo) {
        return Objects.equals(this.userNo, usersNo);
    }
}
