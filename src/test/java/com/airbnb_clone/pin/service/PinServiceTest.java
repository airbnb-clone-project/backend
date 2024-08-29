package com.airbnb_clone.pin.service;

import com.airbnb_clone.common.testcontainer.MongoDBTestContainer;
import com.airbnb_clone.pin.domain.InnerTempPin;
import com.airbnb_clone.pin.domain.PinTemp;
import com.airbnb_clone.pin.domain.dto.request.TemporaryPinCreateRequestDTO;
import com.airbnb_clone.pin.repository.PinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataMongoTest
@DisplayName("핀 서비스 테스트")
@ActiveProfiles("test")
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

    @Test
    @DisplayName("임시 핀 생성 성공 케이스")
    public void When_createTemporaryPin_Expect_Success() {
        // given
        PinTemp pinTemp = PinTemp.of(FirstImageRequestOfFirstUser.getUserNo(), List.of(InnerTempPin.of(FirstImageRequestOfFirstUser.getImageUrl())));

        // when
        pinService.createTempPin(FirstImageRequestOfFirstUser);

        // then
        PinTemp foundTempPin = pinRepository.findPinTempByUserNo(1L).get();

        assertThat(foundTempPin.getUserNo()).isEqualTo(pinTemp.getUserNo());
        assertThat(foundTempPin.getInnerTempPins().get(0).getImgUrl()).isEqualTo(pinTemp.getInnerTempPins().get(0).getImgUrl());
    }

    @Test
    @DisplayName("이미 존재하는 핀에 임시 핀 추가 시 케이스")
    public void When_addTemporaryPinToAlreadySavedPin_Expect_Success() {
        // given
        PinTemp alreadySavedPin = PinTemp.of(FirstImageRequestOfFirstUser.getUserNo(), List.of(InnerTempPin.of(FirstImageRequestOfFirstUser.getImageUrl())));
        mt.save(alreadySavedPin);

        // when
        pinService.createTempPin(SecondImageRequestOfFirstUser);

        // then
        PinTemp foundTempPin = pinRepository.findPinTempByUserNo(1L).get();

        assertThat(foundTempPin.getUserNo()).isEqualTo(alreadySavedPin.getUserNo());
        assertThat(foundTempPin.getInnerTempPins().size()).isEqualTo(2);
        assertThat(foundTempPin.getInnerTempPins().get(1).getImgUrl()).isEqualTo(SecondImageRequestOfFirstUser.getImageUrl());
    }
}
