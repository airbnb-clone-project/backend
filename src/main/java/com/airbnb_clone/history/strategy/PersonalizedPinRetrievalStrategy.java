package com.airbnb_clone.history.strategy;

import com.airbnb_clone.history.domain.PersonalHistory;
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
public class PersonalizedPinRetrievalStrategy implements PinRetrievalStrategy {
    private final PinRedisRepository pinRedisRepository;

    /**
     * 사용자의 히스토리를 기반으로 핀 조회
     *
     * @param historyCounts 히스토리 카운트
     * @param userNo        사용자 번호
     * @param page          페이징 페이지
     * @param pageSize      페이징 페이지 크기
     * @return 조회된 핀 목록
     */
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

        // 총 핀의 개수가 pageSize와 일치하지 않으면 조정
        adjustPinCountToMatchPageSize(pageSize, pinPerCategory, ratios);

        // 각 분류별로 핀을 가져온다.
        // 페이징을 위한 offset 계산
        return pinPerCategory.entrySet().stream()
                .flatMap(entry -> {
                    String category = entry.getKey();
                    int limit = entry.getValue();
                    int offset = page * limit; // 페이징을 위한 offset 계산

                    Pageable pageable = Pageable.ofSize(limit).withPage(offset);

                    return pinRedisRepository.findMainPinHashByImageClassification(category, pageable).stream();
                })
                .map(MainPinHash::toPinMainResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * 페이징 크기와 조회된 핀의 개수가 다를 경우 조회된 핀의 개수를 맞춰준다.
     *
     * @param pageSize       페이징 크기
     * @param pinPerCategory 각 분류별로 핀의 개수
     * @param ratios         각 분류별로 비율
     */
    private static void adjustPinCountToMatchPageSize(int pageSize, Map<String, Integer> pinPerCategory, Map<String, Double> ratios) {
        int totalPins = pinPerCategory.values().stream().mapToInt(Integer::intValue).sum();
        if (totalPins < pageSize) {
            // 부족한 핀의 개수를 가장 큰 비율을 가진 카테고리에 추가
            String maxCategory = ratios.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .get()
                    .getKey();

            pinPerCategory.put(maxCategory, pinPerCategory.get(maxCategory) + (pageSize - totalPins));
        }
    }
}
