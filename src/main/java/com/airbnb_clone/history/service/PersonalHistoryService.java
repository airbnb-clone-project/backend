package com.airbnb_clone.history.service;

import com.airbnb_clone.history.domain.PersonalHistoryHash;
import com.airbnb_clone.history.repository.PersonalHistoryRedisRepository;
import com.airbnb_clone.pin.domain.pin.dto.response.PinMainResponseDTO;
import com.airbnb_clone.pin.repository.PinMySQLRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private final PersonalHistoryRedisRepository personalHistoryRedisRepository;
    private final PinMySQLRepository pinMySQLRepository;

    //TODO 레디스에 해당 회원에 대한 방문 이력을 저장하는 메서드
    public void saveHistory(Long userNo, Long pinNo) {
        //TODO FIX -> 레디스에 저장하는 로직 추가

        PinMainResponseDTO foundPin = pinMySQLRepository.findPinByNo(pinNo);
        //
        personalHistoryRedisRepository.save(new PersonalHistoryHash(userNo, pinNo));
    }
}
