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
public class Tag extends BaseTime {
    private Long no;
    private String name;

    private Tag(LocalDateTime createdAt, LocalDateTime updatedAt, Long no, String name) {
        super(createdAt, updatedAt);
        this.no = no;
        this.name = name;
    }
}