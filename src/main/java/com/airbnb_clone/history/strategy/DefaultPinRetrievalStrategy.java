package com.airbnb_clone.history.strategy;

import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.pin.PinRetrievalException;
import com.airbnb_clone.pin.domain.pin.dto.response.PinMainResponseDTO;
import com.airbnb_clone.pin.domain.pin.redis.MainPinHash;
import com.airbnb_clone.pin.repository.PinRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        if (historyCounts == null || historyCounts.isEmpty()) {
            Pageable pageable = Pageable.ofSize(pageSize).withPage(page);

            return pinRedisRepository.findAll(pageable).getContent().stream()
                    .map(MainPinHash::toPinMainResponseDTO)
                    .collect(Collectors.toList());
        }

        throw new PinRetrievalException(ErrorCode.PIN_RETRIEVAL_EXCEPTION);
    }
}
