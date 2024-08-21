package com.airbnb_clone.pin.domain;

import com.airbnb_clone.common.BaseTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
}
