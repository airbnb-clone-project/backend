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
public class CommentLike extends BaseTime {
    private Long no;
    private Long targetCommentNo;
    private Long liker;
    private int emojiNo;

    private CommentLike(LocalDateTime createdAt, LocalDateTime updatedAt, Long no,
                       Long targetCommentNo, Long liker, int emojiNo) {
        super(createdAt, updatedAt);
        this.no = no;
        this.targetCommentNo = targetCommentNo;
        this.liker = liker;
        this.emojiNo = emojiNo;
    }
}