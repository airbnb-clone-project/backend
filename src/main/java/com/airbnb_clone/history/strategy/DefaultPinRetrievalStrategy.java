package com.airbnb_clone.history.strategy;

import com.airbnb_clone.pin.domain.pin.dto.response.PinMainResponseDTO;
import com.airbnb_clone.pin.repository.PinRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * packageName    : com.airbnb_clone.history.strategy
 * fileName       : DefaultPinRetrievalStrategy
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
public class DefaultPinRetrievalStrategy implements PinRetrievalStrategy {
    private final PinRedisRepository pinRedisRepository;

    //default 이미지지 분류별 핀 가져오기
    @Override
    public List<PinMainResponseDTO> retrievePins(Map<String, Integer> historyCounts, Long limitPerCategory, int page, int pageSize) {
        return Collections.emptyList();
    }
}
