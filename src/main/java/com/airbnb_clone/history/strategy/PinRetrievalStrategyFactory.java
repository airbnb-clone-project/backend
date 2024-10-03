package com.airbnb_clone.history.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * packageName    : com.airbnb_clone.history.strategy
 * fileName       : PinRetrievalStrategyFactory
 * author         : ipeac
 * date           : 24. 10. 3.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 10. 3.        ipeac       최초 생성
 */
@Component
@RequiredArgsConstructor
public class PinRetrievalStrategyFactory {
    private final DefaultPinRetrievalStrategy defaultPinRetrievalStrategy;
    private final PersonalizedPinRetrievalStrategy personalizedPinRetrievalStrategy;

    public PinRetrievalStrategy getPinRetrievalStrategy(Map<String, Integer> historyCounts) {
        //기본 이미지 분류별 핀 가져오기
        if (historyCounts == null || historyCounts.isEmpty()) {
            return defaultPinRetrievalStrategy;
        }

        //사용자의 분류별 핀 가져오기
        return personalizedPinRetrievalStrategy;
    }

}
