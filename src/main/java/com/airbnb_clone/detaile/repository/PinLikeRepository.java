package com.airbnb_clone.detaile.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PinLikeRepository {
    private final JdbcTemplate jdbcTemplate;

    //핀반응추가
    public void addPinLike(Long pinNo, Long userNo, int emojiNo){
        String sql = """
                INSERT INTO  pin_like (TARGET_PIN_NO, LIKER, EMOJI_NO, CREATED_AT )\
                        VALUES (?, ?, ?,CURRENT_TIMESTAMP);
                """;
        try {
            jdbcTemplate.update(sql, pinNo, userNo, emojiNo);
        } catch (DataAccessException e) {
            throw new RuntimeException("핀반응 등록 중 오류 발생", e);
        }
    }

    //추가한 핀반응인지 확인 / 반응이 있는 경우 이모지번호 가져오기
    public Integer getUserEmojiForPin(Long pinNo, Long userNo) {
        String sql = """
                SELECT EMOJI_NO FROM PIN_LIKE WHERE TARGET_PIN_NO = ? AND LIKER = ?
                """;
        try{
            return jdbcTemplate.queryForObject(sql, Integer.class, pinNo, userNo);
        }catch (EmptyResultDataAccessException e) {
            return null;//반응이 없는 경우
        }
    }

    //이모지 반응 업데이트
    public void updatePinLike(Long pinNo, Long userNo, int emojiNo) {
        String sql = """
              UPDATE pin_like
              SET EMOJI_NO = ?, UPDATED_AT = CURRENT_TIMESTAMP
              WHERE TARGET_PIN_NO = ? AND LIKER = ?;
              """;
        try {
            jdbcTemplate.update(sql, pinNo, userNo, emojiNo);
        } catch (DataAccessException e) {
            throw new RuntimeException("핀반응 업데이트 중 오류 발생", e);
        }
    }

    //핀반응삭제
    public void deletePinLike(Long pinNo, Long userNo){
        String sql = """
                    DELETE FROM pin_like WHERE TARGET_PIN_NO = ? AND LIKER = ?;
                """;
        try{
            jdbcTemplate.update(sql, pinNo, userNo);
        }catch (DataAccessException e) {
            throw new RuntimeException("핀반응 삭제 중 오류 발생");
        }
    }


    //핀반응 이모지 변경 or 핀반응 추가에 로직 넣기
}

