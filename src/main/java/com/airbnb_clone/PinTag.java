package com.airbnb_clone;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class PinTag {
    private Long no;
    private Long pinNo;
    private Long tagNo;
}