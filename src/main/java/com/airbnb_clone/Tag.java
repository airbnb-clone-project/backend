package com.airbnb_clone;

import com.airbnb_clone.common.BaseTime;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class Tag extends BaseTime {
    private Long no;
    private String name;

    @Builder
    public Tag(LocalDateTime createdAt, LocalDateTime updatedAt, Long no, String name) {
        super(createdAt, updatedAt);
        this.no = no;
        this.name = name;
    }
}