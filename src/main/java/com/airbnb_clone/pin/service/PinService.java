package com.airbnb_clone.pin.service;

import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.pin.PinNotFoundException;
import com.airbnb_clone.pin.domain.InnerTempPin;
import com.airbnb_clone.pin.domain.PinTemp;
import com.airbnb_clone.pin.domain.dto.request.TemporaryPinCreateRequestDTO;
import com.airbnb_clone.pin.domain.dto.request.TemporaryPinUpdateRequestDTO;
import com.airbnb_clone.pin.domain.dto.response.TemporaryPinDetailResponseDTO;
import com.airbnb_clone.pin.domain.dto.response.TemporaryPinsResponseDTO;
import com.airbnb_clone.pin.repository.PinRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

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
@Transactional(readOnly = true)
public class PinService {
    private final PinRepository pinRepository;

    @Transactional(readOnly = false)
    public ObjectId createTempPin(TemporaryPinCreateRequestDTO temporaryPinCreateRequestDTO) {
        return pinRepository.findPinTempByUserNo(temporaryPinCreateRequestDTO.getUserNo())
                .map(pinTemp -> pinRepository.addInnerTempPinAndGetTempPinId(temporaryPinCreateRequestDTO.getUserNo(), temporaryPinCreateRequestDTO.getImageUrl()))
                .orElseGet(() -> {
                    InnerTempPin insertedInnserTempPin = InnerTempPin.of(temporaryPinCreateRequestDTO.getImageUrl());

                    PinTemp createdTempPin = PinTemp.of(temporaryPinCreateRequestDTO.getUserNo(), Set.of(insertedInnserTempPin));

                    pinRepository.saveAndGetPinId(createdTempPin);

                    return insertedInnserTempPin.get_id();
                });
    }

    public TemporaryPinDetailResponseDTO getTempPin(String tempPinNo) {
        InnerTempPin foundInnerPin = pinRepository.findInnerTempPinById(new ObjectId(tempPinNo)).orElseThrow(() -> new PinNotFoundException(ErrorCode.PIN_NOT_FOUND));

        return foundInnerPin.toTemporaryPinDetailResponseDTO();
    }

    public List<TemporaryPinsResponseDTO> getTempPins(@NotNull Long userNo) {
        PinTemp foundTempPin = pinRepository.findPinTempByUserNo(userNo).orElseThrow(() -> new PinNotFoundException(ErrorCode.PIN_NOT_FOUND));

        return foundTempPin.getInnerTempPins().stream()
                .map(InnerTempPin::toTemporaryPinsResponseDTO)
                .toList();
    }

    @Transactional(readOnly = false)
    public void updateTempPin(@NotNull String tempPinNo, TemporaryPinUpdateRequestDTO temporaryPinCreateRequestDTO) {
        pinRepository.findInnerTempPinById(new ObjectId(tempPinNo)).orElseThrow(()-> new PinNotFoundException(ErrorCode.PIN_NOT_FOUND));

        pinRepository.updateInnerTempPin(new ObjectId(tempPinNo), temporaryPinCreateRequestDTO);
    }
}
