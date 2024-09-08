package com.airbnb_clone.pin.domain.tag;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

/**
 * packageName    : com.airbnb_clone.pin.domain.tag
 * fileName       : Category
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
public class Category {
    @Id
    @Column("NO")
    private Long no;

    @Column("NAME")
    private String name;

    @Column("PARENT_NO")
    private Long parentNo;

    public static Category of(String name, Long parentNo) {
        return Category.builder()
                .name(name)
                .parentNo(parentNo)
                .build();
    }
}
