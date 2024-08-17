package com.airbnb_clone;

import com.airbnb_clone.common.BaseTime;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class PinLike extends BaseTime {
    private Long no;
    private Long targetPinNo;
    private Long liker;
    private int emojiNo;

    @Builder
    public PinLike(LocalDateTime createdAt, LocalDateTime updatedAt, Long no,
                   Long targetPinNo, Long liker, int emojiNo) {
        super(createdAt, updatedAt);
        this.no = no;
        this.targetPinNo = targetPinNo;
        this.liker = liker;
        this.emojiNo = emojiNo;
    }
}