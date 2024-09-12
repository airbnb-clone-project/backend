package com.airbnb_clone.pin.service;

import com.airbnb_clone.common.annotation.DataMongoTestAnnotation;
import com.airbnb_clone.common.testcontainer.MongoDBTestContainer;
import com.airbnb_clone.exception.pin.PinNotFoundException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataMongoTestAnnotation
@DisplayName("임시 핀 서비스 테스트")
public class PinTempServiceTest extends MongoDBTestContainer {

    @MockBean
    private PinMySQLRepository pinMySQLRepository;

    @MockBean
    private TagMySQLRepository tagMySQLRepository;

    @MockBean
    private PinRedisRepository pinRedisRepository;

    @Autowired
    private MongoTemplate mt;
    private PinMongoRepository pinMongoRepository;
    private PinService pinService;

    private TemporaryPinCreateRequestDTO FirstImageRequestOfFirstUser;
    private TemporaryPinCreateRequestDTO SecondImageRequestOfFirstUser;

    @BeforeEach
    void setUp() {
        pinMongoRepository = new PinMongoRepository(mt);
        pinService = new PinService(pinMongoRepository, pinMySQLRepository, pinRedisRepository, tagMySQLRepository);
        FirstImageRequestOfFirstUser = TemporaryPinCreateRequestDTO.of("http://example.com/image.jpg", 1L);
        SecondImageRequestOfFirstUser = TemporaryPinCreateRequestDTO.of("http://example.com/image2.jpg", 1L);

        mt.dropCollection(PinTemp.class);
    }

    @Nested
    @DisplayName("임시 핀 생성 테스트")
    class CreateTempPinTest {
        @Test
        @DisplayName("임시 핀 생성 성공 케이스")
        public void When_createTemporaryPin_Expect_Success() {
            // given
            InnerTempPin expectedInnerTempPin = InnerTempPin.of(FirstImageRequestOfFirstUser.getImageUrl());

            PinTemp expectedPinTemp = PinTemp.of(FirstImageRequestOfFirstUser.getUserNo(), Set.of(expectedInnerTempPin));

            // when
            pinService.createTempPin(FirstImageRequestOfFirstUser);

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
            // given
            InnerTempPin e1 = InnerTempPin.of(FirstImageRequestOfFirstUser.getImageUrl());
            mt.save(e1);

            PinTemp alreadySavedPin = PinTemp.of(FirstImageRequestOfFirstUser.getUserNo(), Set.of(e1));

            mt.save(alreadySavedPin);

            // when
            pinService.createTempPin(SecondImageRequestOfFirstUser);

            // then
            PinTemp foundTempPin = pinMongoRepository.findPinTempByUserNo(1L).get();

            assertThat(foundTempPin.getUserNo()).isEqualTo(alreadySavedPin.getUserNo());
            assertThat(foundTempPin.getInnerTempPins().size()).isEqualTo(2);
            assertThat(foundTempPin.getInnerTempPins().stream().skip(1).findFirst().get().getImgUrl()).isEqualTo(SecondImageRequestOfFirstUser.getImageUrl());

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
            InnerTempPin e1 = InnerTempPin.of(FirstImageRequestOfFirstUser.getImageUrl());
            mt.save(e1);

            PinTemp pinTemp = PinTemp.of(FirstImageRequestOfFirstUser.getUserNo(), Set.of(e1));
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
            InnerTempPin e1 = InnerTempPin.of(FirstImageRequestOfFirstUser.getImageUrl());
            InnerTempPin e2 = InnerTempPin.of(SecondImageRequestOfFirstUser.getImageUrl());

            PinTemp pinTemp = PinTemp.of(FirstImageRequestOfFirstUser.getUserNo(), Set.of(e1, e2));
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
            InnerTempPin e1 = InnerTempPin.of(FirstImageRequestOfFirstUser.getImageUrl());
            mt.save(e1);

            PinTemp savedTempPin = PinTemp.of(FirstImageRequestOfFirstUser.getUserNo(), Set.of(e1));
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
