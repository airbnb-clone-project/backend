package com.airbnb_clone.pin.service;

import com.airbnb_clone.common.annotation.DataMongoTestAnnotation;
import com.airbnb_clone.common.testcontainer.MongoDBTestContainer;
import com.airbnb_clone.exception.pin.PinNotFoundException;
import com.airbnb_clone.history.service.PersonalHistoryService;
import com.airbnb_clone.history.strategy.PinRetrievalStrategyFactory;
import com.airbnb_clone.image.dto.response.ImageClassificationResponseDTO;
import com.airbnb_clone.image.enums.ImageClassificationEnum;
import com.airbnb_clone.image.facade.S3ImageFacade;
import com.airbnb_clone.pin.domain.pin.InnerTempPin;
import com.airbnb_clone.pin.domain.pin.PinTemp;
import com.airbnb_clone.pin.domain.pin.dto.request.TemporaryPinCreateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.request.TemporaryPinUpdateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.response.TemporaryPinDetailResponseDTO;
import com.airbnb_clone.pin.domain.pin.dto.response.TemporaryPinsResponseDTO;
import com.airbnb_clone.pin.repository.PinMongoRepository;
import com.airbnb_clone.pin.repository.PinMySQLRepository;
import com.airbnb_clone.pin.repository.PinRedisRepository;
import com.airbnb_clone.pin.repository.TagMySQLRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DataMongoTestAnnotation
@ExtendWith(MockitoExtension.class)
@DisplayName("임시 핀 서비스 테스트")
public class PinTempServiceTest extends MongoDBTestContainer {

    @MockBean
    private PinMySQLRepository pinMySQLRepository;

    @MockBean
    private TagMySQLRepository tagMySQLRepository;

    @MockBean
    private S3ImageFacade s3ImageFacade;

    @MockBean
    private PinRedisRepository pinRedisRepository;

    @MockBean
    private PinRetrievalStrategyFactory pinRetrievalStrategyFactory;

    @MockBean
    private PersonalHistoryService personalHistoryService;

    @Autowired
    private MongoTemplate mt;
    private PinMongoRepository pinMongoRepository;
    private PinService pinService;

    private TemporaryPinCreateRequestDTO FirstImageRequestOfFirstUser;
    private TemporaryPinCreateRequestDTO SecondImageRequestOfFirstUser;



    @BeforeEach
    void setUp() {
        pinMongoRepository = new PinMongoRepository(mt);
        pinService = new PinService(s3ImageFacade, pinRetrievalStrategyFactory, personalHistoryService, pinMongoRepository, pinMySQLRepository, pinRedisRepository, tagMySQLRepository);

        MockMultipartFile mockFile = new MockMultipartFile(
                "imageFile",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image content".getBytes()
        );

        FirstImageRequestOfFirstUser = TemporaryPinCreateRequestDTO.of(mockFile);
        SecondImageRequestOfFirstUser = TemporaryPinCreateRequestDTO.of(mockFile);

        mt.dropCollection(PinTemp.class);
    }

    @Nested
    @DisplayName("임시 핀 생성 테스트")
    class CreateTempPinTest {
        @Test
        @DisplayName("임시 핀 생성 성공 케이스")
        public void When_createTemporaryPin_Expect_Success() {
            //given
            MultipartFile mockFile = mock(MultipartFile.class);
            ImageClassificationResponseDTO mockResponse = ImageClassificationResponseDTO.of("http://example.com", ImageClassificationEnum.ART);

            // S3ImageFacade.uploadAndClassifyImage 메서드 모킹
            given(s3ImageFacade.uploadAndClassifyImage(any(MultipartFile.class)))
                    .willReturn(Mono.just(mockResponse));

            InnerTempPin expectedInnerTempPin = InnerTempPin.of("http://example.com", ImageClassificationEnum.ART);

            PinTemp expectedPinTemp = PinTemp.of(1L, Set.of(expectedInnerTempPin));

            // when
            Mono<ObjectId> result = pinService.createTempPin(FirstImageRequestOfFirstUser, 1L);

            // then
            StepVerifier.create(result)
                    .expectNextCount(1)
                    .verifyComplete();

            // then
            PinTemp foundTempPin = pinMongoRepository.findPinTempByUserNo(1L).get();

            assertThat(foundTempPin.getUserNo()).isEqualTo(expectedPinTemp.getUserNo());
            assertThat(foundTempPin.getInnerTempPins().stream().findFirst().get().getImgUrl()).isEqualTo(expectedPinTemp.getInnerTempPins().stream().findFirst().get().getImgUrl());

            //내부에 임시 핀 id 가 생성되었는지 확인
            assertThat(foundTempPin.getInnerTempPins().stream().findFirst().get().get_id()).isNotNull();
        }

        @Test
        @DisplayName("이미 존재하는 핀에 임시 핀 추가 시 케이스")
        public void When_addTemporaryPinToAlreadySavedPin_Expect_Success() {
            //given
            MultipartFile mockFile = mock(MultipartFile.class);
            ImageClassificationResponseDTO mockResponse = ImageClassificationResponseDTO.of("http://example.com", ImageClassificationEnum.ART);

            // S3ImageFacade.uploadAndClassifyImage 메서드 모킹
            given(s3ImageFacade.uploadAndClassifyImage(any(MultipartFile.class)))
                    .willReturn(Mono.just(mockResponse));

            InnerTempPin e1 = InnerTempPin.of("http://example.com", ImageClassificationEnum.ART);
            mt.save(e1);

            PinTemp alreadySavedPin = PinTemp.of(1L, Set.of(e1));

            mt.save(alreadySavedPin);

            // when
            Mono<ObjectId> result = pinService.createTempPin(SecondImageRequestOfFirstUser, 1L);

            // when - 비동기 처리를 위한 StepVerifier 사용
            StepVerifier.create(result)
                    .expectNextCount(1)
                    .verifyComplete();

            // then
            PinTemp foundTempPin = pinMongoRepository.findPinTempByUserNo(1L).get();

            assertThat(foundTempPin.getUserNo()).isEqualTo(alreadySavedPin.getUserNo());
            assertThat(foundTempPin.getInnerTempPins().size()).isEqualTo(2);
            assertThat(foundTempPin.getInnerTempPins().stream().skip(1).findFirst().get().getImgUrl()).isEqualTo("http://example.com");

            //내부에 임시 핀 id 가 생성되었는지 확인
            assertThat(foundTempPin.getInnerTempPins().stream().skip(1).findFirst().get().get_id()).isNotNull();
        }
    }

    @Nested
    @DisplayName("임시 핀 조회 테스트")
    class ReadTempPinTest {
        @Test
        @DisplayName("임시 핀 조회 성공 케이스")
        public void When_getTemporaryPin_Expect_Success() {
            // given
            InnerTempPin e1 = InnerTempPin.of("http://example.com", ImageClassificationEnum.ART);
            mt.save(e1);

            PinTemp pinTemp = PinTemp.of(1L, Set.of(e1));
            mt.save(pinTemp);

            // when
            TemporaryPinDetailResponseDTO foundInnerPin = pinService.getTempPin(String.valueOf(pinTemp.getInnerTempPins().stream().findFirst().get().get_id()));

            // then
            assertThat(foundInnerPin.getImgUrl()).isEqualTo(pinTemp.getInnerTempPins().stream().findFirst().get().getImgUrl());
            assertThat(foundInnerPin.getCreatedAt()).isNotNull();
            assertThat(foundInnerPin.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("임시 핀 리스트 조회 성공 케이스")
        public void When_getTemporaryPins_Expect_Success() {
            // given
            InnerTempPin e1 = InnerTempPin.of("http://example.com", ImageClassificationEnum.ART);
            InnerTempPin e2 = InnerTempPin.of("http://example2.com", ImageClassificationEnum.ART);

            PinTemp pinTemp = PinTemp.of(1L, Set.of(e1, e2));
            mt.save(pinTemp);

            // when
            List<TemporaryPinsResponseDTO> foundInnerPins = pinService.getTempPins(1L);

            // then
            assertThat(foundInnerPins.size()).isEqualTo(2);

            assertThat(foundInnerPins)
                    .extracting(TemporaryPinsResponseDTO::getImgUrl)
                    .containsExactlyInAnyOrder(e1.getImgUrl(), e2.getImgUrl());

            assertThat(foundInnerPins).allSatisfy(pin -> {
                assertThat(pin.getCreatedAt()).isNotNull();
                assertThat(pin.getUpdatedAt()).isNotNull();
                assertThat(pin.getTempPinNo()).isNotNull();
            });
        }
    }

    @Nested
    @DisplayName("임시 핀 수정 테스트")
    class UpdateTempPinTest {
        @Test
        @DisplayName("임시 핀 수정 성공 케이스")
        public void When_updateTemporaryPin_Expect_Success() {
            // given
            InnerTempPin e1 = InnerTempPin.of("http://example.com", ImageClassificationEnum.ART);
            mt.save(e1);

            PinTemp savedTempPin = PinTemp.of(1L, Set.of(e1));
            mt.save(savedTempPin);

            ObjectId savedInnerPinObjectId = savedTempPin.getInnerTempPins().stream().findFirst().get().get_id();

            TemporaryPinUpdateRequestDTO updateInnerPinInfo = TemporaryPinUpdateRequestDTO.of(1, "description", "title", true, "http://example.com");

            // when
            pinService.updateTempPin(String.valueOf(savedInnerPinObjectId), updateInnerPinInfo);

            // then
            PinTemp actual = mt.findOne(new Query(Criteria.where("temp_pins").elemMatch(Criteria.where("_id").is(savedInnerPinObjectId))), PinTemp.class);
            InnerTempPin foundInnerPin = actual.getInnerTempPins().stream().findFirst().get();

            assertThat(foundInnerPin.getBoardNo()).isEqualTo(1);
            assertThat(foundInnerPin.getDescription()).isEqualTo("description");
            assertThat(foundInnerPin.getTitle()).isEqualTo("title");
            assertThat(foundInnerPin.isCommentAllowed()).isTrue();
            assertThat(foundInnerPin.getLink()).isEqualTo("http://example.com");
            assertThat(foundInnerPin.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 임시 핀 수정 시도 시 예외 발생 케이스")
        public void When_updateNonExistTemporaryPin_Expect_Exception() {
            // given
            ObjectId nonExistObjectId = new ObjectId();

            TemporaryPinUpdateRequestDTO updateInnerPinInfo = TemporaryPinUpdateRequestDTO.of(1, "title", "description", true, "http://example.com");

            // when & then
            assertThatThrownBy(() -> pinService.updateTempPin(String.valueOf(nonExistObjectId), updateInnerPinInfo))
                    .isInstanceOf(PinNotFoundException.class)
                    .hasMessageContaining("존재하지 않는 핀입니다.");
        }
    }
}
