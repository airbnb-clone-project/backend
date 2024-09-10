package com.airbnb_clone.pin.service;

import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.pin.PinNotFoundException;
import com.airbnb_clone.exception.tag.TagNotFoundException;
import com.airbnb_clone.pin.domain.pin.InnerTempPin;
import com.airbnb_clone.pin.domain.pin.PinTemp;
import com.airbnb_clone.pin.domain.pin.dto.request.PinCreateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.request.PinUpdateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.request.TemporaryPinCreateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.request.TemporaryPinUpdateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.response.TemporaryPinDetailResponseDTO;
import com.airbnb_clone.pin.domain.pin.dto.response.TemporaryPinsResponseDTO;
import com.airbnb_clone.pin.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

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
@Validated
public class PinService {
    private static final int CACHE_SIZE = 1000;

    private final PinMongoRepository pinMongoRepository;
    private final PinMySQLRepository pinMySQLRepository;
    private final PinRedisRepository pinRedisRepository;

    private final TagMySQLRepository tagMySQLRepository;

    @Transactional(readOnly = false)
    public ObjectId createTempPin(@Valid TemporaryPinCreateRequestDTO temporaryPinCreateRequestDTO) {
        return pinMongoRepository.findPinTempByUserNo(temporaryPinCreateRequestDTO.getUserNo())
                .map(pinTemp -> pinMongoRepository.addInnerTempPinAndGetTempPinId(temporaryPinCreateRequestDTO.getUserNo(), temporaryPinCreateRequestDTO.getImageUrl()))
                .orElseGet(() -> {
                    InnerTempPin insertedInnserTempPin = InnerTempPin.of(temporaryPinCreateRequestDTO.getImageUrl());

                    PinTemp createdTempPin = PinTemp.of(temporaryPinCreateRequestDTO.getUserNo(), Set.of(insertedInnserTempPin));

                    pinMongoRepository.saveAndGetPinId(createdTempPin);

                    return insertedInnserTempPin.get_id();
                });
    }

    public TemporaryPinDetailResponseDTO getTempPin(String tempPinNo) {
        InnerTempPin foundInnerPin = pinMongoRepository.findInnerTempPinById(new ObjectId(tempPinNo)).orElseThrow(() -> new PinNotFoundException(ErrorCode.PIN_NOT_FOUND));

        return foundInnerPin.toTemporaryPinDetailResponseDTO();
    }

    public List<TemporaryPinsResponseDTO> getTempPins(@NotNull Long userNo) {
        PinTemp foundTempPin = pinMongoRepository.findPinTempByUserNo(userNo).orElseThrow(() -> new PinNotFoundException(ErrorCode.PIN_NOT_FOUND));

        return foundTempPin.getInnerTempPins().stream()
                .map(InnerTempPin::toTemporaryPinsResponseDTO)
                .toList();
    }

    @Transactional(readOnly = false)
    public void updateTempPin(@NotNull String tempPinNo, TemporaryPinUpdateRequestDTO temporaryPinCreateRequestDTO) {
        pinMongoRepository.findInnerTempPinById(new ObjectId(tempPinNo)).orElseThrow(() -> new PinNotFoundException(ErrorCode.PIN_NOT_FOUND));

        pinMongoRepository.updateInnerTempPin(new ObjectId(tempPinNo), temporaryPinCreateRequestDTO);
    }

    /**
     * 핀 생성
     *
     * @param pinCreateRequestDTO Pin 생성 요청 DTO
     * @return 생성된 Pin 번호
     */
    public Long savePin(@Valid PinCreateRequestDTO pinCreateRequestDTO) {
        vaildateTagIsExist(pinCreateRequestDTO);

        Long insertedPinNo = pinMySQLRepository.savePinAndGetId(pinCreateRequestDTO.toVO());

        tagMySQLRepository.savePinTags(insertedPinNo, pinCreateRequestDTO.getTagNos());

        return insertedPinNo;
    }

    private void vaildateTagIsExist(PinCreateRequestDTO pinCreateRequestDTO) {
        boolean isNotExistTag = !tagMySQLRepository.existsTagByNoIn(pinCreateRequestDTO.getTagNos());
        if(isNotExistTag) {
            throw new TagNotFoundException(ErrorCode.TAG_NOT_FOUND);
        }
    }

    /**
     * 핀 수정
     *
     * @param pinNo 핀 번호
     * @param pinUpdateRequestDTO 핀 수정 요청 DTO
     * @return 수정된 핀 번호
     */
    @Transactional(readOnly = false)
    public Long updatePin(Long pinNo, @Valid PinUpdateRequestDTO pinUpdateRequestDTO) {
        if (!pinMySQLRepository.existsPinByNo(pinNo)) {
            throw new PinNotFoundException(ErrorCode.UPDATE_PIN_NOT_FOUND);
        }

        return pinMySQLRepository.updatePinAndGetId(pinNo, pinUpdateRequestDTO);
    }

    /**
     * 핀 소프트 딜리트
     *
     * @param pinNo 삭제할 핀 번호
     */
    @Transactional(readOnly = false)
    public void deletePinSoftly(Long pinNo) {
        if (!pinMySQLRepository.existsPinByNo(pinNo)) {
            throw new PinNotFoundException(ErrorCode.DELETE_PIN_NOT_FOUND);
        }

        pinMySQLRepository.deletePinSoftly(pinNo);
    }

    /**
     * 적절한 개수의 핀을 Redis에 캐싱
     */
    @Transactional(readOnly = false)
    public void cacheAllPinsToRedis() {
        int offset = 0;
        while (true) {
            List<PinMainResponseDTO> pinsToCached = pinMySQLRepository.findPinsToCached(offset, CACHE_SIZE);
            if (pinsToCached.isEmpty()) {
                break;
            }

            pinRedisRepository.saveAll(pinsToCached.stream()
                    .map(PinMainResponseDTO::toMainPinHash)
                    .toList());

            offset += CACHE_SIZE;
        }
    }
}
