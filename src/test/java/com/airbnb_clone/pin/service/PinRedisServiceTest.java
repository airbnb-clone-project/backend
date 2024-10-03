package com.airbnb_clone.pin.service;

import com.airbnb_clone.common.testcontainer.RedisTestContainer;
import com.airbnb_clone.history.domain.PersonalHistoryHash;
import com.airbnb_clone.history.repository.PersonalHistoryRedisRepository;
import com.airbnb_clone.history.service.PersonalHistoryService;
import com.airbnb_clone.history.strategy.PinRetrievalStrategyFactory;
import com.airbnb_clone.image.enums.ImageClassificationEnum;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayName("핀 메모리 캐싱 서비스 테스트")
public class PinRedisServiceTest extends RedisTestContainer {
    @Autowired
    private PinService pinService;

    @MockBean
    private PinMongoRepository pinMongoRepository;

    @MockBean
    private PinMySQLRepository pinMySQLRepository;

    @MockBean
    private TagMySQLRepository tagMySQLRepository;

    @Autowired
    private PinRedisRepository pinRedisRepository;

    @Autowired
    private PinRetrievalStrategyFactory pinRetrievalStrategyFactory;

    @MockBean
    private S3ImageFacade s3ImageFacade;

    @Autowired
    private PersonalHistoryService personalHistoryService;

    @Autowired
    private PersonalHistoryRedisRepository personalHistoryRedisRepository;

    @BeforeEach
    void setUp() {
        pinRedisRepository.deleteAll();
        personalHistoryRedisRepository.deleteAll();
    }

    @Nested
    @DisplayName("핀 레디스 저장 테스트")
    class CreateTest {
        @Test
        @DisplayName("메인 화면 레디스에 올바르게 저장되는지 테스트")
        void saveMainPin() {
            // given
            given(pinService.findPinsToCached(anyInt())).willReturn(List.of(
                    PinMainResponseDTO.of(1, "http://example.com/image.jpg", "http://example.com", "자연", LocalDateTime.now(), LocalDateTime.now()))).willReturn(List.of());

            // when
            pinService.cacheAllPinsToRedis();

            // then
            Iterable<MainPinHash> foundMainPin = pinRedisRepository.findAll();
            assertThat(foundMainPin).hasSize(1);

            MainPinHash firstMainPin = foundMainPin.iterator().next();

            assertThat(firstMainPin.getPinNo()).isEqualTo("1");
            assertThat(firstMainPin.getImageUrl()).isEqualTo("http://example.com/image.jpg");
            assertThat(firstMainPin.getLink()).isEqualTo("http://example.com");
            assertThat(firstMainPin.getCreatedAt()).isNotNull();
            assertThat(firstMainPin.getUpdatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("핀 조회 테스트")
    class RetrievePinsTest {
        @Test
        @DisplayName("메인 핀 정상 조회 테스트 - 히스토리가 있는 경우")
        void When_RetrieveMainPins_Expect_Success() {
            // given
            //히스토리 10개 추가 (NATURE 5, ART 3, NIGHT_VIEW 2)
            List<PersonalHistoryHash> personalHistory = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                personalHistory.add(PersonalHistoryHash.of(1L, ImageClassificationEnum.NATURE.getKoreanName()));
            }

            for (int i = 0; i < 3; i++) {
                personalHistory.add(PersonalHistoryHash.of(1L, ImageClassificationEnum.ART.getKoreanName()));
            }

            for (int i = 0; i < 2; i++) {
                personalHistory.add(PersonalHistoryHash.of(1L, ImageClassificationEnum.NIGHT_VIEW.getKoreanName()));
            }

            personalHistoryRedisRepository.saveAll(personalHistory);

            // 레디스에 핀 캐싱 수행
            List<MainPinHash> mainPinHashes = new ArrayList<>();

            // 예술 5개 추가
            for (int i = 1; i <= 5; i++) {
                mainPinHashes.add(MainPinHash.of(String.valueOf(i), "http://example.com/image.jpg", "http://example.com", 1L, ImageClassificationEnum.ART.getKoreanName(), LocalDateTime.now(), LocalDateTime.now()));
            }

            // 자연 5개 추가
            for (int i = 6; i <= 10; i++) {
                mainPinHashes.add(MainPinHash.of(String.valueOf(i), "http://example.com/image.jpg", "http://example.com", 1L, ImageClassificationEnum.NATURE.getKoreanName(), LocalDateTime.now(), LocalDateTime.now()));
            }

            // 야경 5개 추가
            for (int i = 11; i <= 15; i++) {
                mainPinHashes.add(MainPinHash.of(String.valueOf(i), "http://example.com/image.jpg", "http://example.com", 1L, ImageClassificationEnum.NIGHT_VIEW.getKoreanName(), LocalDateTime.now(), LocalDateTime.now()));
            }

            pinRedisRepository.saveAll(mainPinHashes);

            //when
            List<PinMainResponseDTO> foundPins = pinService.getMainPins(1L, 0, 5);

            assertThat(foundPins).hasSize(5)
                    .satisfies(pins -> {
                        Map<String, Long> classificationCounts = foundPins.stream()
                                .collect(Collectors.groupingBy(PinMainResponseDTO::getImageClassification, Collectors.counting()));

                        assertThat(classificationCounts).containsEntry("자연", 3L);
                        assertThat(classificationCounts).containsEntry("예술", 1L);
                        assertThat(classificationCounts).containsEntry("야경", 1L);
                    });
        }

        @Test
        @DisplayName("메인 핀 정상 조회 테스트 - 히스토리가 없는 경우")
        void When_RetrieveMainPinsNoHistory_Expect_Success() {
            // given
            // 레디스에 핀 캐싱 수행
            List<MainPinHash> mainPinHashes = new ArrayList<>();

            // 예술 5개 추가
            for (int i = 1; i <= 5; i++) {
                mainPinHashes.add(MainPinHash.of(String.valueOf(i), "http://example.com/image.jpg", "http://example.com", 1L, ImageClassificationEnum.ART.getKoreanName(), LocalDateTime.now(), LocalDateTime.now()));
            }

            // 자연 5개 추가
            for (int i = 6; i <= 10; i++) {
                mainPinHashes.add(MainPinHash.of(String.valueOf(i), "http://example.com/image.jpg", "http://example.com", 1L, ImageClassificationEnum.NATURE.getKoreanName(), LocalDateTime.now(), LocalDateTime.now()));
            }

            // 야경 5개 추가
            for (int i = 11; i <= 15; i++) {
                mainPinHashes.add(MainPinHash.of(String.valueOf(i), "http://example.com/image.jpg", "http://example.com", 1L, ImageClassificationEnum.NIGHT_VIEW.getKoreanName(), LocalDateTime.now(), LocalDateTime.now()));
            }

            pinRedisRepository.saveAll(mainPinHashes);

            //when
            List<PinMainResponseDTO> foundPins = pinService.getMainPins(1L, 0, 5);

            // then
            assertThat(foundPins).hasSize(5)
                    .allSatisfy(pinMainResponseDTO -> {
                        assertThat(pinMainResponseDTO.getImageClassification()).isEqualTo("예술");
                    });
        }
    }
}
