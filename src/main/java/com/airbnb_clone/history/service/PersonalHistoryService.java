package com.airbnb_clone.history.service;

import com.airbnb_clone.history.domain.PersonalHistoryHash;
import com.airbnb_clone.history.repository.PersonalHistoryRedisRepository;
import com.airbnb_clone.pin.domain.pin.dto.response.PinHistoryResponseDTO;
import com.airbnb_clone.pin.repository.PinMySQLRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName    : com.airbnb_clone.history.service
 * fileName       : PersonalHistoryService
 * author         : ipeac
 * date           : 24. 9. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 29.        ipeac       최초 생성
 */
@Service
@RequiredArgsConstructor
public class PersonalHistoryService {
    public static final int HISTORY_LIMIT = 10;
    private final PersonalHistoryRedisRepository personalHistoryRedisRepository;
    private final PinMySQLRepository pinMySQLRepository;

    /**
     * 사용자의 분류별 카운트를 제공한다. 키 : 밸류 구조로 각 classification 별로 카운트를 제공한다.
     * @return 분류별 카운트
     */
    public Map<String, Integer> getHistoryCounts(@NonNull Long userNo) {
        List<PersonalHistoryHash> foundHistories = List.copyOf(personalHistoryRedisRepository.findByUserNoOrderByVisitedAtDesc(userNo));

        final Map<String, Integer> historyCounts = new HashMap<>();

        //각 방문이력 분류별 카운트를 합산한다.
        for (PersonalHistoryHash foundHistory : foundHistories) {
            String classification = foundHistory.getVisitedHistoryClassification();
            historyCounts.put(classification, historyCounts.getOrDefault(classification, 0) + 1);
        }

        return historyCounts;
    }

    public void saveHistory(Long userNo, Long pinNo) {
        PinHistoryResponseDTO foundPin = pinMySQLRepository.findPinForHistoryByNo(pinNo);

        List<PersonalHistoryHash> existingHistories = personalHistoryRedisRepository.findByUserNoOrderByVisitedAtDesc(userNo);

        deleteIfHistoryOverLimit(existingHistories);

        // 새로운 방문 이력 저장
        personalHistoryRedisRepository.save(PersonalHistoryHash.from(foundPin, userNo));
    }

    private void deleteIfHistoryOverLimit(List<PersonalHistoryHash> existingHistories) {
        if(existingHistories.size() >= HISTORY_LIMIT) {
            PersonalHistoryHash oldestHistory = existingHistories.get(0);
            personalHistoryRedisRepository.delete(oldestHistory);
        }
    }
}
