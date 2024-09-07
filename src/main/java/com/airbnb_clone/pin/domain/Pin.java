package com.airbnb_clone.pin.domain;

import com.airbnb_clone.common.BaseTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Pin extends BaseTime implements Serializable {
    private Long no;
    private Long userNo;
    private String imgUrl;
    private String title;
    private String description;
    private String link;
    private Long boardNo;
    private boolean isCommentAllowed;
    private int likeCount;
}
