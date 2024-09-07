package com.airbnb_clone.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * packageName    : com.airbnb_clone.common
 * fileName       : BaseTime
 * author         : sjunpark
 * date           : 24. 8. 16.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 16.        sjunpark       최초 생성
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@SuperBuilder
public class BaseTime {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
