package com.airbnb_clone.detaile.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PinLikeRepository {
    private final JdbcTemplate jdbcTemplate;

    //핀반응추가
    public void addPinlike(Long pinNo, Long userNo, int emojiNo){
        String sql = """
                INSERT INTO  pin_like (TARGET_PIN_NO, LIKER, EMOJI_NO, CREATED_AT )\
                        VALUES (?, ?, ?,CURRENT_TIMESTAMP);
                """;
        try {
            jdbcTemplate.update(sql, pinNo, userNo, emojiNo);
        } catch (DataAccessException e) {
            throw new RuntimeException("핀 등록 중 오류 발생", e);
        }
    }

    public boolean hasUserReactedToPin(Long pinNo, Long userNo) {
        String sql = """
                SELECT COUNT(*) FROM PIN_LIKE WHERE TARGET_PIN_NO = ? AND LIKER = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, pinNo, userNo);
        return count != null && count > 0;

    }
}

