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
public class Board extends BaseTime {
    private Long no;
    private String name;
    private Long userNo;

    @Builder
    public Board(LocalDateTime createdAt, LocalDateTime updatedAt, Long no, String name, Long userNo) {
        super(createdAt, updatedAt);
        this.no = no;
        this.name = name;
        this.userNo = userNo;
    }
}