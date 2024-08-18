package com.airbnb_clone;

import com.airbnb_clone.common.BaseTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Pin extends BaseTime {
    private Long no;
    private String imgUrl;
    private String title;
    private String description;
    private String link;
    private Long boardNo;
    private boolean isCommentAllowed;
    private int likeCount;

    private Pin(LocalDateTime createdAt, LocalDateTime updatedAt, Long no, String imgUrl,
               String title, String description, String link, Long boardNo,
               boolean isCommentAllowed, int likeCount) {
        super(createdAt, updatedAt);
        this.no = no;
        this.imgUrl = imgUrl;
        this.title = title;
        this.description = description;
        this.link = link;
        this.boardNo = boardNo;
        this.isCommentAllowed = isCommentAllowed;
        this.likeCount = likeCount;
    }
}