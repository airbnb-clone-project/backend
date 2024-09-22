package com.airbnb_clone.pin.service;

import com.airbnb_clone.common.testcontainer.RedisTestContainer;
import com.airbnb_clone.image.facade.S3ImageFacade;
import com.airbnb_clone.pin.domain.pin.dto.response.PinMainResponseDTO;
import com.airbnb_clone.pin.domain.pin.redis.MainPinHash;
import com.airbnb_clone.pin.repository.PinMongoRepository;
import com.airbnb_clone.pin.repository.PinMySQLRepository;
import com.airbnb_clone.pin.repository.PinRedisRepository;
import com.airbnb_clone.pin.repository.TagMySQLRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

/**
 * packageName    : com.airbnb_clone.pin.service
 * fileName       : PinRedisServiceTest
 * author         : ipeac
 * date           : 24. 9. 12.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 12.        ipeac       최초 생성
 */
@DataRedisTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PinRedisServiceTest extends RedisTestContainer {
    @InjectMocks
    private PinService pinService;

    @Mock
    private PinMongoRepository pinMongoRepository;

    @Mock
    private PinMySQLRepository pinMySQLRepository;

    @Mock
    private TagMySQLRepository tagMySQLRepository;

    @Autowired
    private PinRedisRepository pinRedisRepository;

    @MockBean
    private S3ImageFacade s3ImageFacade;

    @BeforeEach
    void setUp() {
        pinRedisRepository.deleteAll();

        pinService = new PinService(s3ImageFacade, pinMongoRepository, pinMySQLRepository, pinRedisRepository, tagMySQLRepository);
    }

    @Nested
    @DisplayName("핀 레디스 저장 테스트")
    class CreateTest {
        @Test
        @DisplayName("메인 화면 레디스에 올바르게 저장되는지 테스트")
        void saveMainPin() {
            // given
            given(pinService.findPinsToCached(anyInt(), anyInt())).willReturn(List.of(
                    PinMainResponseDTO.of(1L, "http://example.com/image.jpg", "http://example.com", LocalDateTime.now(), LocalDateTime.now()))).willReturn(List.of());

            // when
            pinService.cacheAllPinsToRedis();

            // then
            Iterable<MainPinHash> foundMainPin = pinRedisRepository.findAll();
            assertThat(foundMainPin).hasSize(1);

            MainPinHash firstMainPin = foundMainPin.iterator().next();

            assertThat(firstMainPin.getPinNo()).isEqualTo(1L);
            assertThat(firstMainPin.getImageUrl()).isEqualTo("http://example.com/image.jpg");
            assertThat(firstMainPin.getLink()).isEqualTo("http://example.com");
            assertThat(firstMainPin.getCreatedAt()).isNotNull();
            assertThat(firstMainPin.getUpdatedAt()).isNotNull();
        }
    }
}
