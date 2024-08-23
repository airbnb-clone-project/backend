package com.airbnb_clone.auth.repository;

import com.airbnb_clone.auth.domain.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * packageName    : com.airbnb_clone.auth.repository;
 * fileName       : RefreshTokenRepository
 * author         : DK
 * date           : 24. 8. 22.
 * description    : 발급된 Refresh token 을 저장하는 repository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 22.        DK       최초 생성
 */
@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final JdbcTemplate jdbcTemplate;

    public void saveRefreshToken(RefreshToken token) {
        String sql = "INSERT INTO Refresh_Token (username, refresh, expiration) " +
                "VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sql,
                    token.getUsername(),
                    token.getRefreshToken(),
                    token.getExpiration()
            );

        } catch (DataAccessException e) {

            throw new RuntimeException("토큰 등록 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    // 같은 내용의 토큰이 있으면 true 반환
    public Boolean existsByRefresh(String token) {
        String sql = "SELECT * FROM refresh_token WHERE refresh = ?"; // 같은 내용의 토큰 있는지 확인
        return jdbcTemplate.query(sql, (rs, nowNum) -> 0, token).isEmpty();
    }

    // 같은 내용의 토큰 삭제
    public void deleteRefreshToken(String token) {
        String sql = "DELETE FROM refresh_token WHERE refresh = ?";
        try {
            jdbcTemplate.update(sql, token);
        } catch (DataAccessException e) {
            throw new RuntimeException("토큰 삭제 중 오류가 발생했습니다." + e.getMessage());
        }
    }

    // 토큰 저장

}