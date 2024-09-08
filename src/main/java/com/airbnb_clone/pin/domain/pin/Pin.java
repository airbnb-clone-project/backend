package com.airbnb_clone.pin.domain.pin;

import com.airbnb_clone.Tag;
import com.airbnb_clone.common.BaseTime;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashSet;
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
    private int likeCount;
}
