package com.airbnb_clone.pin.service;

import com.airbnb_clone.common.annotation.DataMongoTestAnnotation;
import com.airbnb_clone.common.testcontainer.MongoDBTestContainer;
import com.airbnb_clone.config.s3.AwsS3Config;
import com.airbnb_clone.pin.domain.InnerTempPin;
import com.airbnb_clone.pin.domain.PinTemp;
import com.airbnb_clone.pin.domain.dto.request.TemporaryPinCreateRequestDTO;
import com.airbnb_clone.pin.domain.dto.response.TemporaryPinDetailResponseDTO;
import com.airbnb_clone.pin.repository.PinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataMongoTestAnnotation
@Import(AwsS3Config.class)
@DisplayName("핀 서비스 테스트")
public class PinServiceTest extends MongoDBTestContainer {
    @Autowired
    private MongoTemplate mt;
    private PinRepository pinRepository;
    private PinService pinService;

    private TemporaryPinCreateRequestDTO FirstImageRequestOfFirstUser;
    private TemporaryPinCreateRequestDTO SecondImageRequestOfFirstUser;

    @BeforeEach
    void setUp() {
        pinRepository = new PinRepository(mt);
        pinService = new PinService(pinRepository);
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
            PinTemp foundTempPin = pinRepository.findPinTempByUserNo(1L).get();

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
            PinTemp foundTempPin = pinRepository.findPinTempByUserNo(1L).get();

            assertThat(foundTempPin.getUserNo()).isEqualTo(alreadySavedPin.getUserNo());
            assertThat(foundTempPin.getInnerTempPins().size()).isEqualTo(2);
            assertThat(foundTempPin.getInnerTempPins().stream().skip(1).findFirst().get().getImgUrl()).isEqualTo(SecondImageRequestOfFirstUser.getImageUrl());

            //내부에 임시 핀 id 가 생성되었는지 확인
            assertThat(foundTempPin.getInnerTempPins().stream().skip(1).findFirst().get().get_id()).isNotNull();
        }
    }

    @Nested
    @DisplayName("임시 핀 조회 테스트")
    class GetTempPinTest {
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
    }
}
