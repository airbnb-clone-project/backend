package com.airbnb_clone.history.strategy;

import com.airbnb_clone.history.domain.PersonalHistory;
import com.airbnb_clone.pin.domain.pin.dto.response.PinMainResponseDTO;
import com.airbnb_clone.pin.domain.pin.redis.MainPinHash;
import com.airbnb_clone.pin.repository.PinRedisRepository;
import lombok.RequiredArgsConstructor;
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
public class PersonalizedPinRetrievalStrategy implements PinRetrievalStrategy {
    private final PinRedisRepository pinRedisRepository;

    @Override
    public List<PinMainResponseDTO> retrievePins(Map<String, Integer> historyCounts, Long userNo, int page, int pageSize) {
        PersonalHistory personalHistory = PersonalHistory.from(historyCounts);

        //각 분류 비율 계산
        Map<String, Double> ratios = personalHistory.getHistoryCount().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue() / (double) personalHistory.getTotalCount()
                ));

        //각 분류별로 가져올 핀의 개수를 계산한다.
        Map<String, Integer> pinPerCategory = ratios.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> (int) (e.getValue() * pageSize)
                ));

        // 각 분류별로 핀을 가져온다.
        // 페이징을 위한 offset 계산
        return pinPerCategory.entrySet().stream()
                .flatMap(entry -> {
                    String category = entry.getKey();
                    int limit = entry.getValue();
                    int offset = page * limit; // 페이징을 위한 offset 계산
                    return pinRedisRepository.findPinsByImageClassification(category, offset, limit).stream();
                })
                .map(MainPinHash::toPinMainResponseDTO)
                .collect(Collectors.toList());
    }
}
