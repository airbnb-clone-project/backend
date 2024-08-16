package com.airbnb_clone;

import com.airbnb_clone.common.BaseTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * packageName    : com.airbnb_clone
 * fileName       : Tag
 * author         : sjunpark
 * date           : 24. 8. 16.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 16.        sjunpark       최초 생성
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Tag extends BaseTime {
    private Long no;
    private String name;
}