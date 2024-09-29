package com.airbnb_clone.history.service;

import com.airbnb_clone.common.testcontainer.RedisTestContainer;
import com.airbnb_clone.history.domain.PersonalHistoryHash;
import com.airbnb_clone.history.repository.PersonalHistoryRedisRepository;
import com.airbnb_clone.pin.domain.pin.Pin;
import com.airbnb_clone.pin.repository.PinMySQLRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName    : com.airbnb_clone.history.service
 * fileName       : PersonalHistoryServiceTest
 * author         : ipeac
 * date           : 24. 9. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 29.        ipeac       최초 생성
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = {"/schema/pin.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("개인 방문 이력 서비스 테스트")
class PersonalHistoryServiceTest extends RedisTestContainer {
    private PersonalHistoryService personalHistoryService;

    @Autowired
    private PersonalHistoryRedisRepository personalHistoryRedisRepository;

    @Autowired
    private PinMySQLRepository pinMySQLRepository;


    @BeforeEach
    void setUp() {
        personalHistoryService = new PersonalHistoryService(personalHistoryRedisRepository, pinMySQLRepository);
        personalHistoryRedisRepository.deleteAll();
    }

    @Test
    @DisplayName("방문 이력 저장 성공 테스트")
    void When_SaveHistory_Expect_Success() {
        // given
        Pin insertPin = Pin.builder()
                .userNo(1L)
                .boardNo(1L)
                .imageClassification("test")
                .imgUrl("test.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isPinDeleted(false)
                .build();

        Long insertedPinNo = pinMySQLRepository.savePinAndGetId(insertPin);
        Long userNo = 1L;

        // when
        personalHistoryService.saveHistory(userNo, insertedPinNo);

        // then
        List<PersonalHistoryHash> foundHistory = personalHistoryRedisRepository.findByUserNoOrderByVisitedAtDesc(userNo);

        assertThat(foundHistory).isNotNull();
        assertThat(foundHistory.size()).isEqualTo(1);
        assertThat(foundHistory.get(0).getVisitedHistoryClassification()).isEqualTo("test");
        assertThat(foundHistory.get(0).getUserNo()).isEqualTo(1L);
        assertThat(foundHistory.get(0).getVisitedAt()).isNotNull();
    }
}
