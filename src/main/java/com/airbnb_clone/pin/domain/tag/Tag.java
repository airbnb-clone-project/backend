package com.airbnb_clone.pin.domain.tag;

import com.airbnb_clone.common.BaseTime;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

/**
 * packageName    : com.airbnb_clone.pin.domain
 * fileName       : Tag
 * author         : ipeac
 * date           : 24. 9. 8.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 8.        ipeac       최초 생성
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Tag extends BaseTime {
    @Id
    @Column("NO")
    private Long no;

    @Column("CATEGORY_NO")
    private Long categoryNo;

    public static Tag of(Long categoryNo) {
        return Tag.builder()
                .categoryNo(categoryNo)
                .build();
    }
}
