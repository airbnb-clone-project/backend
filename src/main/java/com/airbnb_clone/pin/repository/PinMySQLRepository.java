package com.airbnb_clone.pin.repository;

import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.pin.PinNotFoundException;
import com.airbnb_clone.pin.domain.pin.Pin;
import com.airbnb_clone.pin.domain.pin.dto.request.PinUpdateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.response.PinHistoryResponseDTO;
import com.airbnb_clone.pin.domain.pin.dto.response.PinMainResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * packageName    : com.airbnb_clone.pin.repository
 * fileName       : PinMySQLRepository
 * author         : ipeac
 * date           : 24. 9. 7.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 7.        ipeac       최초 생성
 */
@Repository
@RequiredArgsConstructor
public class PinMySQLRepository {
    private final NamedParameterJdbcTemplate jt;

    public PinHistoryResponseDTO findPinForHistoryByNo(Long pinNo) {
        String sql = """
                SELECT NO, IMAGE_CLASSIFICATION, CREATED_AT \
                FROM PIN \
                WHERE NO = :no AND IS_PIN_DELETED = FALSE""";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("no", pinNo);
        
        PinHistoryResponseDTO result = jt.queryForObject(sql, parameters, (rs, rowNum) -> PinHistoryResponseDTO.of(
                rs.getLong("NO"),
                rs.getString("IMAGE_CLASSIFICATION"),
                rs.getTimestamp("CREATED_AT").toLocalDateTime()
        ));
        
        return Optional.ofNullable(result).orElseThrow(() -> new PinNotFoundException(ErrorCode.PIN_NOT_FOUND));
    }

    public boolean existsPinByNo(Long pinNo) {
        String sql = "SELECT COUNT(NO) FROM PIN WHERE NO = :no";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("no", pinNo);

        int count = Objects.requireNonNull(jt.queryForObject(sql, parameters, Integer.class));

        return count == 1;
    }

    public Long savePinAndGetId(Pin savePin) {
        String sql = """
                INSERT INTO PIN (USER_NO, IMG_URL, TITLE, DESCRIPTION, LINK, BOARD_NO, IS_COMMENT_ALLOWED, IMAGE_CLASSIFICATION, CREATED_AT, UPDATED_AT) \
                VALUES (:userNo, :imgUrl, :title, :description, :link, :boardNo, :isCommentAllowed, :imageClassification, :createdAt, :updatedAt)""";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("userNo", savePin.getUserNo())
                .addValue("imgUrl", savePin.getImgUrl())
                .addValue("title", savePin.getTitle())
                .addValue("description", savePin.getDescription())
                .addValue("link", savePin.getLink())
                .addValue("boardNo", savePin.getBoardNo())
                .addValue("isCommentAllowed", savePin.isCommentAllowed())
                .addValue("imageClassification", savePin.getImageClassification())
                .addValue("createdAt", Timestamp.valueOf(savePin.getCreatedAt()))
                .addValue("updatedAt", Timestamp.valueOf(savePin.getUpdatedAt()));

        jt.update(sql, parameters, keyHolder, new String[]{"NO"});

        return  Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public Long updatePinAndGetId(Long pinNo, PinUpdateRequestDTO updatePin) {
        String sql = """
                UPDATE PIN \
                SET TITLE = :title, DESCRIPTION = :description, LINK = :link, BOARD_NO = :boardNo, IS_COMMENT_ALLOWED = :isCommentAllowed, UPDATED_AT = :updatedAt \
                WHERE NO = :no""";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("no", pinNo)
                .addValue("title", updatePin.getTitle())
                .addValue("description", updatePin.getDescription())
                .addValue("link", updatePin.getLink())
                .addValue("boardNo", updatePin.getBoardNo())
                .addValue("isCommentAllowed", updatePin.isCommentAllowed())
                .addValue("updatedAt", LocalDateTime.now());

        jt.update(sql, parameters);

        return pinNo;
    }

    public void deletePinSoftly(Long pinNo) {
        String sql = "UPDATE PIN SET IS_PIN_DELETED = TRUE WHERE NO = :no";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("no", pinNo);

        jt.update(sql, parameters);
    }

    public List<PinMainResponseDTO> findPinsToCached(int limitPerCategory) {
        String sql = """
                SELECT NO, IMG_URL, LINK, IMAGE_CLASSIFICATION, CREATED_AT, UPDATED_AT
                FROM (
                    SELECT NO, IMG_URL, LINK, IMAGE_CLASSIFICATION, CREATED_AT, UPDATED_AT,
                           ROW_NUMBER() OVER (PARTITION BY IMAGE_CLASSIFICATION ORDER BY CREATED_AT DESC) AS rn
                    FROM PIN
                    WHERE IS_PIN_DELETED = FALSE
                ) sub
                WHERE sub.rn <= :limitPerCategory
                """;

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("limitPerCategory", limitPerCategory);

        return jt.query(sql, parameters, (rs, rowNum) -> PinMainResponseDTO.of(
                rs.getLong("NO"),
                rs.getString("IMG_URL"),
                rs.getString("LINK"),
                rs.getString("IMAGE_CLASSIFICATION"),
                rs.getTimestamp("CREATED_AT").toLocalDateTime(),
                rs.getTimestamp("UPDATED_AT").toLocalDateTime()
        ));
    }

    public void saveAll(List<Pin> savePins) {
        String sql = """
                INSERT INTO PIN (USER_NO, IMG_URL, TITLE, DESCRIPTION, LINK, BOARD_NO, IS_COMMENT_ALLOWED, CREATED_AT, UPDATED_AT) \
                VALUES (:userNo, :imgUrl, :title, :description, :link, :boardNo, :isCommentAllowed, :createdAt, :updatedAt)""";

        List<MapSqlParameterSource> parameters = savePins.stream()
                .map(pin -> new MapSqlParameterSource()
                        .addValue("userNo", pin.getUserNo())
                        .addValue("imgUrl", pin.getImgUrl())
                        .addValue("title", pin.getTitle())
                        .addValue("description", pin.getDescription())
                        .addValue("link", pin.getLink())
                        .addValue("boardNo", pin.getBoardNo())
                        .addValue("isCommentAllowed", pin.isCommentAllowed())
                        .addValue("createdAt", Timestamp.valueOf(pin.getCreatedAt()))
                        .addValue("updatedAt", Timestamp.valueOf(pin.getUpdatedAt()))
                )
                .toList();

        jt.batchUpdate(sql, parameters.toArray(new MapSqlParameterSource[0]));
    }

    public Optional<Pin> findPinByNo(Long pinNo) {
        String sql = """
                SELECT NO, USER_NO, IMG_URL, TITLE, DESCRIPTION, LINK, BOARD_NO, IS_COMMENT_ALLOWED, IMAGE_CLASSIFICATION, CREATED_AT, UPDATED_AT \
                FROM PIN \
                WHERE NO = :no AND IS_PIN_DELETED = FALSE""";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("no", pinNo);

        return Optional.ofNullable(jt.queryForObject(sql, parameters, (rs, rowNum) -> Pin.builder()
                .no(rs.getLong("NO"))
                .userNo(rs.getLong("USER_NO"))
                .imgUrl(rs.getString("IMG_URL"))
                .title(rs.getString("TITLE"))
                .description(rs.getString("DESCRIPTION"))
                .link(rs.getString("LINK"))
                .boardNo(rs.getLong("BOARD_NO"))
                .isCommentAllowed(rs.getBoolean("IS_COMMENT_ALLOWED"))
                .imageClassification(rs.getString("IMAGE_CLASSIFICATION"))
                .createdAt(rs.getTimestamp("CREATED_AT").toLocalDateTime())
                .updatedAt(rs.getTimestamp("UPDATED_AT").toLocalDateTime())
                .build()));
    }
}
