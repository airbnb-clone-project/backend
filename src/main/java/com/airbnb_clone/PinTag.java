package com.airbnb_clone;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode
public class PinTag {
    private Long no;
    private Long pinNo;
    private Long tagNo;
}