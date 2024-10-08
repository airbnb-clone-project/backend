package com.airbnb_clone.detailPage;


import com.airbnb_clone.detaile.dto.DetailPageDto;
import com.airbnb_clone.detaile.dto.PinLikeDto;
import com.airbnb_clone.detaile.repository.DetailPageRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

@JdbcTest
@ActiveProfiles("test")
class DetailPageRepositoryTest {

    @Autowired
    JdbcTemplate jdbcTemplate;
    DetailPageRepository detailPageRepository;

    @BeforeEach
    void beforeEach() throws IOException {
        detailPageRepository = new DetailPageRepository(jdbcTemplate);

        //스키마 생성 sql 파일 실행
        Resource resource = new ClassPathResource("schema/detailpage.sql");
        String schemaSql = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
        jdbcTemplate.execute(schemaSql);

        // 샘플 데이터 삽입
        jdbcTemplate.update("INSERT INTO users (NO, USERNAME, IS_SOCIAL, PASSWORD, FIRST_NAME, LAST_NAME, PROFILE_IMG_URL) VALUES (1, 'user1', 1, '123', 'first','last', 'profile1.jpg');");
        jdbcTemplate.update("INSERT INTO board (NO, NAME, USER_NO) VALUES (1, 'board1', 1);");
        jdbcTemplate.update("INSERT INTO pin (NO, USER_NO, IMG_URL, TITLE, DESCRIPTION, LINK, BOARD_NO) VALUES (1, 1, 'img1.jpg', 'Title 1', 'Description 1', 'http://example.com', 1);");
        jdbcTemplate.update("INSERT INTO pin_like (NO, TARGET_PIN_NO, EMOJI_NO, CREATED_AT, LIKER) VALUES (1, 1, 1, CURRENT_TIMESTAMP, 1);");
    }

    @Test
    @DisplayName("상세페이지 조회")
    void join_detail_page__success() {
        Long pinId = 1L;
        DetailPageDto detailPageDto = detailPageRepository.findPageById(pinId);

        Assertions.assertThat(detailPageDto).isNotNull();
        Assertions.assertThat(detailPageDto.getPinNo()).isEqualTo(1L);
        Assertions.assertThat(detailPageDto.getImgUrl()).isEqualTo("img1.jpg");
        Assertions.assertThat(detailPageDto.getTitle()).isEqualTo("Title 1");
        Assertions.assertThat(detailPageDto.getDescription()).isEqualTo("Description 1");
        Assertions.assertThat(detailPageDto.getLink()).isEqualTo("http://example.com");
        Assertions.assertThat(detailPageDto.getUserNo()).isEqualTo(1L);
        Assertions.assertThat(detailPageDto.getFirstName()).isEqualTo("first");
        Assertions.assertThat(detailPageDto.getLastName()).isEqualTo("last");
        Assertions.assertThat(detailPageDto.getProfileImgUrl()).isEqualTo("profile1.jpg");

    }

    @Test
    @DisplayName("반응 목록 조회")
    void joun_pinlike_list__success() {
        Long pinId = 1L;
        List<PinLikeDto> pinLikeDtos = detailPageRepository.findPinlikesById(pinId);

        Assertions.assertThat(pinLikeDtos).isNotNull();
        Assertions.assertThat(pinLikeDtos).hasSize(1);

        PinLikeDto pinLikeDto = pinLikeDtos.get(0);
        Assertions.assertThat(pinLikeDto.getPinLikeNo()).isEqualTo(1L);
        Assertions.assertThat(pinLikeDto.getPinNo()).isEqualTo(1L);
        Assertions.assertThat(pinLikeDto.getEmojiNo()).isEqualTo(1);
        Assertions.assertThat(pinLikeDto.getLiker()).isEqualTo(1L);
        Assertions.assertThat(pinLikeDto.getFirstname()).isEqualTo("first");
        Assertions.assertThat(pinLikeDto.getLastname()).isEqualTo("last");
        Assertions.assertThat(pinLikeDto.getUserProfileImg()).isEqualTo("profile1.jpg");
        Assertions.assertThat(pinLikeDto.getCreatedAt()).isNotNull();
    }
}
