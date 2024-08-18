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
public class Comment extends BaseTime {
    private Long no;
    private Long targetPinNo;
    private Long commenterNo;
    private Long parentCommentNo;
    private int depth;
    private String content;
    private int likeCount;

    private Comment(LocalDateTime createdAt, LocalDateTime updatedAt, Long no, Long targetPinNo,
                   Long commenterNo, Long parentCommentNo, int depth, String content, int likeCount) {
        super(createdAt, updatedAt);
        this.no = no;
        this.targetPinNo = targetPinNo;
        this.commenterNo = commenterNo;
        this.parentCommentNo = parentCommentNo;
        this.depth = depth;
        this.content = content;
        this.likeCount = likeCount;
    }
}