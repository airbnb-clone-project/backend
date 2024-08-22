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

            // 로그 기록
            throw new RuntimeException("토큰 등록 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}