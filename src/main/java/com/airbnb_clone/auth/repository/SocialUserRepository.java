package com.airbnb_clone.auth.repository;

import com.airbnb_clone.auth.domain.SocialUser;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * packageName    : com.airbnb_clone.auth.repository
 * fileName       : OAuth2UserRepository
 * author         : doungukkim
 * date           : 2024. 8. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024. 8. 24.        doungukkim       최초 생성
 */
@Repository
@RequiredArgsConstructor
public class SocialUserRepository {

    private final JdbcTemplate jdbcTemplate;

    public void registerSocialUser(SocialUser user) {
        String sql = "INSERT INTO social_user (user_no, provider, provider_id) " +
                "VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sql,
                    user.getUserNo(),
                    user.getProvider(),
                    user.getProviderId()
                    );
        } catch (DataAccessException e) {
            throw new RuntimeException("Social_user 사용자 등록 중 오류가 발생했습니다.", e);
        }
    }

    // 소셜 계정 삭제
    public void deleteSocialAccount(Long userNo) {
        String sql = "DELETE FROM social_user WHERE user_no = ?";
        try {
            jdbcTemplate.update(sql, userNo);
        } catch (DataAccessException e) {
            throw new RuntimeException("토큰 삭제 중 오류가 발생했습니다." + e.getMessage());
        }
    }

    public Optional<SocialUser> findByProviderId(String providerId){
        String sql = "SELECT user_no, provider, provider_id FROM social_user WHERE provider_id = ?";

        try {
            List<SocialUser> users = jdbcTemplate.query(sql, new Object[]{providerId}, new RowMapper<SocialUser>() {
                @Override
                public SocialUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return SocialUser.builder()
                            .userNo(rs.getLong("user_no"))
                            .provider(rs.getString("provider"))
                            .providerId(rs.getString("provider_id"))
                            .build();
                }

            });
            return users.stream().findFirst();
        } catch (DataAccessException e) {
            throw new RuntimeException("social_user 데이터베이스 조회 중 오류가 발생했습니다.", e);
        }
    }
}
