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
public class Notification extends BaseTime {
    private Long no;
    private Long userNo;
    private String type;
    private Long relatedPinNo;
    private boolean isRead;
    private String name;
    private String content;

    private Notification(LocalDateTime createdAt, LocalDateTime updatedAt, Long no, Long userNo,
                        String type, Long relatedPinNo, boolean isRead, String name, String content) {
        super(createdAt, updatedAt);
        this.no = no;
        this.userNo = userNo;
        this.type = type;
        this.relatedPinNo = relatedPinNo;
        this.isRead = isRead;
        this.name = name;
        this.content = content;
    }
}