package com.airbnb_clone.pin.service;

import com.airbnb_clone.pin.domain.Pin;
import com.airbnb_clone.pin.domain.dto.request.PinCreateRequestDTO;
import com.airbnb_clone.pin.repository.PinMongoRepository;
import com.airbnb_clone.pin.repository.PinMySQLRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJdbcTest
@DisplayName("핀 서비스 테스트")
public class PinRealServiceTest {

    private PinMySQLRepository pinMySQLRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private PinMongoRepository pinMongoRepository;

    private PinService pinService;

    private PinCreateRequestDTO firstPinCreateRequestDTO;

    @BeforeEach
    void setUp() {
        pinMySQLRepository = new PinMySQLRepository(jdbcTemplate);
        pinService = new PinService(pinMongoRepository, pinMySQLRepository);

        firstPinCreateRequestDTO = PinCreateRequestDTO.of("http://example.com/image.jpg", "핀 제목", "핀 설명", "핀 링크", 1L, true, LocalDateTime.now());
    }

    @Nested
    @DisplayName("핀 생성 테스트")
    class CreatePinTest {
        @Test
        @DisplayName("핀 생성 성공 케이스")
        void When_CreatePin_Expect_Success() {
            // given

            // when
            Long actualPinNo = pinService.savePin(firstPinCreateRequestDTO);

            // then
            assertThat(actualPinNo).isNotNull();

            String findPinQuery = "SELECT * FROM PIN WHERE NO = ?";

            Pin foundPin = jdbcTemplate.queryForObject(findPinQuery, Pin.class, actualPinNo);

            assertThat(foundPin).isNotNull();
            assertThat(foundPin.getUserNo()).isNotNull().isEqualTo(firstPinCreateRequestDTO.getUserNo());
            assertThat(foundPin.getImgUrl()).isEqualTo(firstPinCreateRequestDTO.getImgUrl());
            assertThat(foundPin.getTitle()).isEqualTo(firstPinCreateRequestDTO.getTitle());
            assertThat(foundPin.getDescription()).isEqualTo(firstPinCreateRequestDTO.getDescription());
            assertThat(foundPin.getLink()).isEqualTo(firstPinCreateRequestDTO.getLink());
            assertThat(foundPin.getBoardNo()).isEqualTo(firstPinCreateRequestDTO.getBoardNo());
            assertThat(foundPin.isCommentAllowed()).isEqualTo(firstPinCreateRequestDTO.isCommentAllowed());
            assertThat(foundPin.getLikeCount()).isEqualTo(0);
            assertThat(foundPin.getCreatedAt()).isEqualTo(firstPinCreateRequestDTO.getCreatedAt());
            assertThat(foundPin.getUpdatedAt()).isEqualTo(firstPinCreateRequestDTO.getCreatedAt());
        }
    }
}
