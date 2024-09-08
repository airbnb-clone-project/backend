package com.airbnb_clone.pin.service;

import com.airbnb_clone.common.annotation.DataJdbcTestAnnotation;
import com.airbnb_clone.pin.domain.pin.dto.request.PinCreateRequestDTO;
import com.airbnb_clone.pin.repository.PinMongoRepository;
import com.airbnb_clone.pin.repository.PinMySQLRepository;
import com.airbnb_clone.pin.repository.TagMySQLRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

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
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @MockBean
    private PinMongoRepository pinMongoRepository;

    private PinService pinService;

    private PinCreateRequestDTO firstPinCreateRequestDTO;

    @BeforeEach
    void setUp() {
        tagMySQLRepository = new TagMySQLRepository(namedParameterJdbcTemplate);
        pinMySQLRepository = new PinMySQLRepository(jdbcTemplate);
        pinService = new PinService(pinMongoRepository, pinMySQLRepository, tagMySQLRepository);

        firstPinCreateRequestDTO = PinCreateRequestDTO.of(1L, "http://example.com/image.jpg", "핀 제목", "핀 설명", "핀 링크", 1L, true, Set.of(1L));
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

            String findPinQuery = "SELECT NO , USER_NO, IMG_URL, TITLE, DESCRIPTION, LINK, BOARD_NO, IS_COMMENT_ALLOWED, LIKE_COUNT, CREATED_AT, UPDATED_AT FROM PIN WHERE NO = ?";
            Map<String, Object> foundPinMap = jdbcTemplate.queryForMap(findPinQuery, actualPinNo);

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
}
