package com.airbnb_clone.pin.service;

import com.airbnb_clone.pin.domain.InnerTempPin;
import com.airbnb_clone.pin.domain.PinTemp;
import com.airbnb_clone.pin.repository.PinRepository;
import com.airbnb_clone.pin.repository.TemporaryPinCreateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * packageName    : com.airbnb_clone.pin.service
 * fileName       : PinService
 * author         : ipeac
 * date           : 24. 8. 21.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 21.        ipeac       최초 생성
 */
@Service
@RequiredArgsConstructor
public class PinService {
    private final PinRepository pinRepository;

    public void createTempPin(TemporaryPinCreateRequestDTO temporaryPinCreateRequestDTO) {
        pinRepository.findPinTempByUserNo(temporaryPinCreateRequestDTO.getUserNo())
                .ifPresentOrElse(
                        pinTemp -> {
                            pinRepository.addInnerTempPin(temporaryPinCreateRequestDTO.getUserNo(), temporaryPinCreateRequestDTO.getImageUrl());
                        },
                        () -> {
                            PinTemp createdTempPin = PinTemp.of(temporaryPinCreateRequestDTO.getUserNo(), List.of(InnerTempPin.of(temporaryPinCreateRequestDTO.getImageUrl())));
                            pinRepository.save(createdTempPin);
                        }
                );
    }
}
