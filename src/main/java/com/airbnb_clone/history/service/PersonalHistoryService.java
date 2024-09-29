package com.airbnb_clone.history.service;

import com.airbnb_clone.history.domain.PersonalHistoryHash;
import com.airbnb_clone.history.repository.PersonalHistoryRedisRepository;
import com.airbnb_clone.pin.domain.pin.dto.response.PinHistoryResponseDTO;
import com.airbnb_clone.pin.repository.PinMySQLRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
