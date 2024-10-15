package com.airbnb_clone.pin.service;

import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.pin.PinNotFoundException;
import com.airbnb_clone.exception.tag.TagNotFoundException;
import com.airbnb_clone.history.service.PersonalHistoryService;
import com.airbnb_clone.history.strategy.PinRetrievalStrategy;
import com.airbnb_clone.history.strategy.PinRetrievalStrategyFactory;
import com.airbnb_clone.image.facade.S3ImageFacade;
import com.airbnb_clone.pin.domain.pin.InnerTempPin;
import com.airbnb_clone.pin.domain.pin.PinTemp;
import com.airbnb_clone.pin.domain.pin.dto.request.PinCreateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.request.PinUpdateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.request.TemporaryPinCreateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.request.TemporaryPinUpdateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.response.PinMainResponseDTO;
import com.airbnb_clone.pin.domain.pin.dto.response.TemporaryPinDetailResponseDTO;
import com.airbnb_clone.pin.domain.pin.dto.response.TemporaryPinsResponseDTO;
import com.airbnb_clone.pin.repository.PinMongoRepository;
import com.airbnb_clone.pin.repository.PinMySQLRepository;
import com.airbnb_clone.pin.repository.PinRedisRepository;
import com.airbnb_clone.pin.repository.TagMySQLRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    public static final int CACHE_SIZE_PER_CATEGORY = 100;

    private final S3ImageFacade s3ImageFacade;
    private final PinRetrievalStrategyFactory pinRetrievalStrategyFactory;

    private final PersonalHistoryService personalHistoryService;

    private final PinMongoRepository pinMongoRepository;
    private final PinMySQLRepository pinMySQLRepository;
    private final PinRedisRepository pinRedisRepository;

    private final TagMySQLRepository tagMySQLRepository;

    @Transactional(readOnly = false)
    public Mono<ObjectId> createTempPin(@Valid TemporaryPinCreateRequestDTO temporaryPinCreateRequestDTO, String userEmail) {
        return s3ImageFacade.uploadAndClassifyImage(temporaryPinCreateRequestDTO.getImageFile())
                .map(imageClassificationResponseDTO -> pinMongoRepository.findPinTempByUserEmail(userEmail)
                        .map(pinTemp -> pinMongoRepository.addInnerTempPinAndGetTempPinId(userEmail, imageClassificationResponseDTO.getImageUrl(),imageClassificationResponseDTO.getImageCategory()))
                        .orElseGet(() -> {
                            InnerTempPin insertedInnserTempPin = InnerTempPin.of(imageClassificationResponseDTO.getImageUrl(), imageClassificationResponseDTO.getImageCategory());

                            PinTemp createdTempPin = PinTemp.of(userEmail, Set.of(insertedInnserTempPin));

                            pinMongoRepository.saveAndGetPinId(createdTempPin);

                            return insertedInnserTempPin.get_id();
                        }));
    }

    public TemporaryPinDetailResponseDTO getTempPin(String tempPinNo) {
        InnerTempPin foundInnerPin = pinMongoRepository.findInnerTempPinById(new ObjectId(tempPinNo)).orElseThrow(() -> new PinNotFoundException(ErrorCode.PIN_NOT_FOUND));

        return foundInnerPin.toTemporaryPinDetailResponseDTO();
    }

    public List<TemporaryPinsResponseDTO> getTempPins(@NotNull String userEmail) {
        Optional<PinTemp> foundTempOpt = pinMongoRepository.findPinTempByUserEmail(userEmail);

        return foundTempOpt.map(pinTemp -> pinTemp.getInnerTempPins().stream()
                .map(InnerTempPin::toTemporaryPinsResponseDTO)
                .toList()).orElseGet(List::of);
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

    public List<PinMainResponseDTO> findPinsToCached(int limitPerCategory) {
        return pinMySQLRepository.findPinsToCached(limitPerCategory);
    }

    /**
     * 분류별 | 최신순으로 핀을 레디스에 특정 개수만큼 캐싱한다.
     */
    @Transactional(readOnly = false)
    public void cacheAllPinsToRedis() {
        List<PinMainResponseDTO> pins = findPinsToCached(CACHE_SIZE_PER_CATEGORY);

        // 캐싱할 핀이 존재할 경우 레디스에 저장
        if (!pins.isEmpty()) {
            pinRedisRepository.saveAll(pins.stream()
                    .map(PinMainResponseDTO::toMainPinHash)
                    .toList());
        }
    }

    /**
     * 메인에 노출할 핀을 레디스에서 조회한다. 히스토리 분류에 따라 노출할 핀의 % 에 따라 메인 노출 핀 가져오기
     *
     * @param page     페이징 페이지
     * @param pageSize 페이징 페이지 크기
     * @param userNo
     * @return 메인 화면에 노출할 핀 목록
     */
    public List<PinMainResponseDTO> getMainPins(int page, int pageSize, Long userNo) {
        Map<String, Integer> historyCounts = Map.of();

        // 유저 정보가 있는 경우 히스토리 분류 수를 가져온다.
        if (userNo != null) {
            historyCounts = personalHistoryService.getHistoryCounts(userNo);
        }

        // 핀 가져오기 전략 선택
        PinRetrievalStrategy pinRetrievalStrategy = pinRetrievalStrategyFactory.getPinRetrievalStrategy(historyCounts);

        return pinRetrievalStrategy.retrievePins(historyCounts, userNo, page, pageSize);
    }
}
