package com.airbnb_clone.pin.domain.tag;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@EqualsAndHashCode
public class PinTag {
    @Id
    @Column("NO")
    private Long no;

    @Column("PIN_NO")
    private Long pinNo;

    @Column("TAG_NO")
    private Long tagNo;

    public static PinTag of(Long pinNo, Long tagNo) {
        return PinTag.builder()
                .pinNo(pinNo)
                .tagNo(tagNo)
                .build();
    }
}
