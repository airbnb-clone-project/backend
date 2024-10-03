package com.airbnb_clone.pin.service;

import com.airbnb_clone.common.annotation.DataJdbcTestAnnotation;
import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.pin.PinNotFoundException;
import com.airbnb_clone.history.service.PersonalHistoryService;
import com.airbnb_clone.history.strategy.PinRetrievalStrategyFactory;
import com.airbnb_clone.image.enums.ImageClassificationEnum;
import com.airbnb_clone.image.facade.S3ImageFacade;
import com.airbnb_clone.pin.domain.pin.Pin;
import com.airbnb_clone.pin.domain.pin.dto.request.PinCreateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.request.PinUpdateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.response.PinMainResponseDTO;
import com.airbnb_clone.pin.repository.PinMongoRepository;
import com.airbnb_clone.pin.repository.PinMySQLRepository;
import com.airbnb_clone.pin.repository.PinRedisRepository;
import com.airbnb_clone.pin.repository.TagMySQLRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DataJdbcTestAnnotation
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
@Sql(scripts = "classpath:schema/pin.sql")
@DisplayName("핀 서비스 테스트")
public class PinRealServiceTest {

    private PinMySQLRepository pinMySQLRepository;

    private TagMySQLRepository tagMySQLRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @MockBean
    private PinMongoRepository pinMongoRepository;

    @MockBean
    private PinRedisRepository pinRedisRepository;

    private PinService pinService;

    @MockBean
    private S3ImageFacade s3ImageFacade;

    @MockBean
    private PinRetrievalStrategyFactory pinRetrievalStrategyFactory;

    @MockBean
    private PersonalHistoryService personalHistoryService;

    private PinCreateRequestDTO firstPinCreateRequestDTO;


    @BeforeEach
    void setUp() {
        tagMySQLRepository = new TagMySQLRepository(namedParameterJdbcTemplate);
        pinMySQLRepository = new PinMySQLRepository(namedParameterJdbcTemplate);

        pinService = new PinService(s3ImageFacade, pinRetrievalStrategyFactory, personalHistoryService, pinMongoRepository, pinMySQLRepository, pinRedisRepository, tagMySQLRepository);

        firstPinCreateRequestDTO = PinCreateRequestDTO.of(1L, "http://example.com/image.jpg", "핀 제목", "핀 설명", "핀 링크", 1L, true, Set.of(1L), ImageClassificationEnum.ART);
    }

    @Nested
    @DisplayName("핀 조회 테스트")
    class ReadPinTest {

        @Test
        @DisplayName("메인 레디스 캐시할 핀 조회 성공 케이스")
        void test1() {
            // given
            //태그 선 생성
            String insertTagQuery = "INSERT INTO TAG (NO, CATEGORY_NO) VALUES (1, 1)";
            namedParameterJdbcTemplate.update(insertTagQuery, new MapSqlParameterSource());

            Pin savePin = Pin.of(1L, "http://example.com/image.jpg", "핀 제목", "핀 설명", "핀 링크", Set.of(), 1L, true, LocalDateTime.now(), LocalDateTime.now(), false, ImageClassificationEnum.ART);

            pinMySQLRepository.savePinAndGetId(savePin);

            // when
            List<PinMainResponseDTO> actualMainPinResponses = pinService.findPinsToCached(1);

            // then
            assertThat(actualMainPinResponses).isNotNull();
            assertSoftly(
                    softly -> {
                        softly.assertThat(actualMainPinResponses.size()).isEqualTo(1);
                        softly.assertThat(actualMainPinResponses.get(0).getLink()).isEqualTo(savePin.getLink());
                        softly.assertThat(actualMainPinResponses.get(0).getImageUrl()).isEqualTo(savePin.getImgUrl());
                        softly.assertThat(actualMainPinResponses.get(0).getCreatedAt()).isNotNull();
                        softly.assertThat(actualMainPinResponses.get(0).getUpdatedAt()).isNotNull();
                    }
            );
        }
    }

    @Nested
    @DisplayName("핀 생성 테스트")
    class CreatePinTest {
        @Test
        @DisplayName("핀 생성 성공 케이스")
        void When_CreatePin_Expect_Success() {
            // given
            //태그 선 생성
            String insertTagQuery = "INSERT INTO TAG (NO, CATEGORY_NO) VALUES (1, 1)";
            namedParameterJdbcTemplate.update(insertTagQuery, new MapSqlParameterSource());

            // when
            Long actualPinNo = pinService.savePin(firstPinCreateRequestDTO);

            // then
            assertThat(actualPinNo).isNotNull();

            String findPinQuery = """
                    SELECT NO , USER_NO, IMG_URL, TITLE, DESCRIPTION, LINK, BOARD_NO, IS_COMMENT_ALLOWED, LIKE_COUNT, CREATED_AT, UPDATED_AT \
                    FROM PIN WHERE NO = :no""";
            Map<String, Object> foundPinMap = namedParameterJdbcTemplate.queryForMap(findPinQuery, new MapSqlParameterSource("no", actualPinNo));

            assertThat(foundPinMap).isNotNull();
            assertThat(foundPinMap.get("NO")).isNotNull().isEqualTo(actualPinNo);
            assertThat(foundPinMap.get("USER_NO")).isNotNull().isEqualTo(firstPinCreateRequestDTO.getUserNo());
            assertThat(foundPinMap.get("IMG_URL")).isEqualTo(firstPinCreateRequestDTO.getImgUrl());
            assertThat(foundPinMap.get("TITLE")).isEqualTo(firstPinCreateRequestDTO.getTitle());
            assertThat(foundPinMap.get("DESCRIPTION")).isEqualTo(firstPinCreateRequestDTO.getDescription());
            assertThat(foundPinMap.get("LINK")).isEqualTo(firstPinCreateRequestDTO.getLink());
            assertThat(foundPinMap.get("BOARD_NO")).isEqualTo(firstPinCreateRequestDTO.getBoardNo());
            assertThat(foundPinMap.get("IS_COMMENT_ALLOWED")).isEqualTo(firstPinCreateRequestDTO.isCommentAllowed());
            assertThat(foundPinMap.get("LIKE_COUNT")).isEqualTo(0);
            assertThat(foundPinMap.get("CREATED_AT")).isNotNull();
            assertThat(foundPinMap.get("UPDATED_AT")).isNotNull();
        }
    }

    @Nested
    @DisplayName("핀 삭제 테스트")
    class DeletePinTest {
        @Test
        @DisplayName("핀 삭제 성공 케이스")
        void When_DeletePin_Expect_Success() {
            // given
            //태그 선 생성
            String insertTagQuery = "INSERT INTO TAG (NO, CATEGORY_NO) VALUES (1, 1)";
            namedParameterJdbcTemplate.update(insertTagQuery, new MapSqlParameterSource());

            Pin savePin = Pin.of(1L, "http://example.com/image.jpg", "핀 제목", "핀 설명", "핀 링크", Set.of(), 1L, true, LocalDateTime.now(), LocalDateTime.now(), false, ImageClassificationEnum.ART);

            Long actualPinNo = pinMySQLRepository.savePinAndGetId(savePin);

            // when
            pinService.deletePinSoftly(actualPinNo);

            // then
            String findPinQuery = """
                    SELECT IS_PIN_DELETED \
                    FROM PIN WHERE NO = :no""";


            Map<String, Object> foundPinMap = namedParameterJdbcTemplate.queryForMap(findPinQuery, new MapSqlParameterSource("no", actualPinNo));

            assertThat(foundPinMap).isNotNull();
            assertThat(foundPinMap.get("IS_PIN_DELETED")).isEqualTo(true);
        }

        @Test
        @DisplayName("존재하지 않는 핀 삭제 시도 시 예외 발생")
        void When_DeleteNotExistsPin_Expect_Exception() {
            // when & then
            assertThatThrownBy(() -> pinService.deletePinSoftly(1L))
                    .isInstanceOf(PinNotFoundException.class)
                    .hasMessage(ErrorCode.DELETE_PIN_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("핀 수정 테스트")
    class UpdatePinTest {
        @Test
        @DisplayName("핀 수정 성공 케이스")
        void When_UpdatePin_Expect_Success() {
            // given
            //태그 선 생성
            String insertTagQuery = "INSERT INTO TAG (NO, CATEGORY_NO) VALUES (1, 1)";
            namedParameterJdbcTemplate.update(insertTagQuery, new MapSqlParameterSource());

            Pin savePin = Pin.of(1L, "http://example.com/image.jpg", "핀 제목", "핀 설명", "핀 링크", Set.of(), 1L, true, LocalDateTime.now(), LocalDateTime.now(), false, ImageClassificationEnum.ART);

            Long actualPinNo = pinMySQLRepository.savePinAndGetId(savePin);

            PinUpdateRequestDTO updatePinCreateRequestDTO = PinUpdateRequestDTO.of("수정된 핀 제목", "수정된 핀 설명", "수정된 핀 링크", 2L, false);

            // when
            Long updatedPinNo = pinService.updatePin(actualPinNo, updatePinCreateRequestDTO);

            // then
            assertThat(updatedPinNo).isNotNull().isEqualTo(actualPinNo);

            String findPinQuery = """
                    SELECT NO , USER_NO, IMG_URL, TITLE, DESCRIPTION, LINK, BOARD_NO, IS_COMMENT_ALLOWED, LIKE_COUNT, CREATED_AT, UPDATED_AT \
                    FROM PIN WHERE NO = :no""";

            Map<String, Object> foundPinMap = namedParameterJdbcTemplate.queryForMap(findPinQuery, new MapSqlParameterSource("no", updatedPinNo));

            assertThat(foundPinMap).isNotNull();
            assertThat(foundPinMap.get("NO")).isNotNull().isEqualTo(actualPinNo);
            assertThat(foundPinMap.get("USER_NO")).isNotNull().isEqualTo(savePin.getUserNo());
            assertThat(foundPinMap.get("IMG_URL")).isEqualTo(savePin.getImgUrl());
            assertThat(foundPinMap.get("TITLE")).isEqualTo(updatePinCreateRequestDTO.getTitle());
            assertThat(foundPinMap.get("DESCRIPTION")).isEqualTo(updatePinCreateRequestDTO.getDescription());
            assertThat(foundPinMap.get("LINK")).isEqualTo(updatePinCreateRequestDTO.getLink());
            assertThat(foundPinMap.get("BOARD_NO")).isEqualTo(updatePinCreateRequestDTO.getBoardNo());
            assertThat(foundPinMap.get("IS_COMMENT_ALLOWED")).isEqualTo(updatePinCreateRequestDTO.isCommentAllowed());
            assertThat(foundPinMap.get("LIKE_COUNT")).isEqualTo(0);
            assertThat(foundPinMap.get("CREATED_AT")).isNotEqualTo(foundPinMap.get("UPDATED_AT"));
        }

        @Test
        @DisplayName("존재하지 않는 핀 수정 시도 시 예외 발생")
        void When_UpdateNotExistsPin_Expect_Exception() {
            // given
            PinUpdateRequestDTO updatePinCreateRequestDTO = PinUpdateRequestDTO.of("수정된 핀 제목", "수정된 핀 설명", "수정된 핀 링크", 2L, false);

            // when & then
            assertThatThrownBy(() -> pinService.updatePin(1L, updatePinCreateRequestDTO))
                    .isInstanceOf(PinNotFoundException.class)
                    .hasMessage(ErrorCode.UPDATE_PIN_NOT_FOUND.getMessage());
        }
    }
}
