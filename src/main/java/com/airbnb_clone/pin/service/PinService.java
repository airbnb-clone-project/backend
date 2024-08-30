package com.airbnb_clone.pin.service;

import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.pin.PinNotFoundException;
import com.airbnb_clone.pin.domain.InnerTempPin;
import com.airbnb_clone.pin.domain.PinTemp;
import com.airbnb_clone.pin.domain.dto.request.TemporaryPinCreateRequestDTO;
import com.airbnb_clone.pin.domain.dto.response.TemporaryPinDetailResponseDTO;
import com.airbnb_clone.pin.repository.PinRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
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

    public ObjectId createTempPin(TemporaryPinCreateRequestDTO temporaryPinCreateRequestDTO) {
        return pinRepository.findPinTempByUserNo(temporaryPinCreateRequestDTO.getUserNo())
                .map(pinTemp -> pinRepository.addInnerTempPinAndGetId(temporaryPinCreateRequestDTO.getUserNo(), temporaryPinCreateRequestDTO.getImageUrl()))
                .orElseGet(() -> {
                    PinTemp createdTempPin = PinTemp.of(temporaryPinCreateRequestDTO.getUserNo(), List.of(InnerTempPin.of(temporaryPinCreateRequestDTO.getImageUrl())));
                    return pinRepository.saveAndGetId(createdTempPin);
                });
    }

    public TemporaryPinDetailResponseDTO getTempPin(String tempPinNo) {
        PinTemp foundTemp = pinRepository.findPinTempById(new ObjectId(tempPinNo)).orElseThrow(() -> new PinNotFoundException(ErrorCode.PIN_NOT_FOUND));
        return foundTemp;
    }
}
