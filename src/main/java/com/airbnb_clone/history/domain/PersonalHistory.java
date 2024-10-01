package com.airbnb_clone.history.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * packageName    : com.airbnb_clone.history.domain
 * fileName       : PersonalHistoryCount
 * author         : ipeac
 * date           : 24. 10. 1.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 10. 1.        ipeac       최초 생성
 */
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class PersonalHistory {
    private final int totalCount;
    private final Map<String,Integer> historyCount;

    public static PersonalHistory from(Map<String, Integer> historyCount) {
        return PersonalHistory.builder()
                .totalCount(historyCount.values().stream().mapToInt(Integer::intValue).sum())
                .historyCount(historyCount)
                .build();
    }
}
