package com.airbnb_clone.history.strategy;

import com.airbnb_clone.pin.domain.pin.dto.response.PinMainResponseDTO;

import java.util.List;
import java.util.Map;

/**
 * packageName    : com.airbnb_clone.history.strategy
 * fileName       : PinRetrievalStrategy
 * author         : ipeac
 * date           : 24. 10. 3.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 10. 3.        ipeac       최초 생성
 */
public interface PinRetrievalStrategy {
    //조회 전략 선택
    List<PinMainResponseDTO> retrievePins(Map<String, Integer> historyCounts, Long userNo, int page, int pageSize);
}
