package com.airbnb_clone.auth.repository;

import com.airbnb_clone.auth.domain.Users;
import com.airbnb_clone.auth.dto.oauth2.MoreUserRegisterRequest;
import com.airbnb_clone.auth.dto.users.UsersProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * packageName    : com.airbnb_clone.auth.repository;
 * fileName       : UserRepository
 * author         : DK
 * date           : 24. 8. 22.
 * description    : 유저 repository
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 22.        DK       최초 생성
 * 24. 8. 31.        DK       계정 추가정보 업데이트 기능 추가
 * 24. 9. 13.        DK       username 기준 profile 정보 가져오는 기능 추가
 */
@Repository
@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public boolean isUsernameNotExist(String username) {
        String readSql = "SELECT * FROM users WHERE username = ?"; // username 있는지 여부 확인
        // 값이 있으면 0을 반환한다. 그리고 이걸 List로 반환한다. [0] 없으면 비어있다. 비어있을때 .isEmpty를 사용해서 true를 반환
        return jdbcTemplate.query(readSql, (rs, rowNum) -> 0, username).isEmpty(); // 실제 데이터를 값을 저장
    }

    // 계정 삭제
    public void deleteAccount(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try {
            jdbcTemplate.update(sql, username);
        } catch (DataAccessException e) {
            throw new RuntimeException("회원 탈퇴 중 오류가 발생했습니다." + e.getMessage());
        }
    }

    // 유저 저장
    public void registerUser(Users user) {
        String sql = "INSERT INTO users (username, password, first_name, last_name, birthday, is_social) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            jdbcTemplate.update(sql,
                    user.getUsername(),
                    user.getPassword(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getBirthday(),
                    user.isSocial()
            );
        } catch (DataAccessException e) {
            throw new RuntimeException("사용자 등록 중 오류가 발생했습니다.", e);
        }
    }

    public void updateUserProfileImage(String username, String imageUrl) {
        String sql = "UPDATE users SET profile_img_url = ? WHERE username = ?";
        jdbcTemplate.update(sql, imageUrl, username);
    }

    public String findProfileImageByUsername(String username) {
        String sql = "SELECT profile_img_url FROM users WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, username);
        } catch (EmptyResultDataAccessException e) {
            // 결과가 없을 경우 null 반환 또는 예외 처리
            return null;
        }
    }
    public void updateUserProfile(UsersProfileRequest request) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, description = ?" +
                "WHERE username = ?";
        try {
            jdbcTemplate.update(sql,
                    request.getFirstName(),
                    request.getLastName(),
                    request.getDescription(),
                    request.getUsername()
            );
        } catch (DataAccessException e) {
            throw new RuntimeException("프로필 정보 등록 중 오류가 발생 했습니다.", e);
        }
    }


    public Long findNoByUsername(String username) {
        String sql = "SELECT no FROM users WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, username);
        } catch (EmptyResultDataAccessException e) {
            // 결과가 없을 경우 null 반환 또는 예외 처리
            return null;
        }
    }

    public LocalDate findBirthdayByUsername(String username) {
        String sql = "SELECT birthday FROM users WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, LocalDate.class, username);
        } catch (EmptyResultDataAccessException e) {
            // 결과가 없을 경우 null 반환 또는 예외 처리
            return null;
        }
    }

    public String findOldPasswordByUsername(String username) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, username);
        } catch (EmptyResultDataAccessException e) {
            // 결과가 없을 경우 null 반환
            return null;
        }
    }

    public void updatePassword(String username, String password) {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        jdbcTemplate.update(sql, password, username);
    }

    public void updateFirstName(String username, String newFirstName) {
        String sql = "UPDATE users SET first_name = ? WHERE username = ?";
        jdbcTemplate.update(sql, newFirstName, username);
    }

    public void saveMoreUserInformation(MoreUserRegisterRequest request) {
        String sql = "UPDATE users SET birthday = ?, gender = ?, spoken_language = ?, country = ? " +
                "WHERE username = ?";
        try {
            jdbcTemplate.update(sql,
                    request.getBirthday(),
                    request.getGender(),
                    request.getSpokenLanguage(),
                    request.getCountry(),
                    request.getUsername()
            );
        } catch (DataAccessException e) {
            throw new RuntimeException("사용자 정보 등록 중 오류가 발생 했습니다.", e);
        }
    }

    public Optional<Users> findUsernamePasswordByUsername(String username) {
        String sql = "SELECT username, password FROM users WHERE username = ?";

        try {
            List<Users> users = jdbcTemplate.query(sql, new Object[]{username}, new RowMapper<Users>() {
                @Override
                public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return Users.builder()
                            .username(rs.getString("username"))
                            .password(rs.getString("password"))
                            .build();
                }
            });
            return users.stream().findFirst();
        } catch (DataAccessException e) {
            throw new RuntimeException("데이터베이스 조회 중 오류가 발생했습니다.", e);
        }
    }

    public Optional<Users> findProfileByUsername(String username) {
        String sql = "SELECT description, first_name,last_name FROM users WHERE username = ?";

        try {
            List<Users> users = jdbcTemplate.query(sql, new Object[]{username}, new RowMapper<Users>() {
                @Override
                public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return Users.builder()
                            .description(rs.getString("description"))
                            .lastName(rs.getString("last_name"))
                            .firstName(rs.getString("first_name"))
                            .build();
                }
            });
            return users.stream().findFirst();
        } catch (DataAccessException e) {
            throw new RuntimeException("데이터베이스 조회 중 오류가 발생했습니다.", e);
        }
    }
    // 이메일 비밀번호 생일 성별 국가 언어
    public Optional<Users> findAccountByUsername(String username) {
        String sql = "SELECT username, birthday,gender, country,spoken_language FROM users WHERE username = ?";

        try {
            List<Users> users = jdbcTemplate.query(sql, new Object[]{username}, new RowMapper<Users>() {
                @Override
                public Users mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return Users.builder()
                            .username(rs.getString("username"))
                            .birthday(rs.getObject("birthday", LocalDate.class))
                            .gender(rs.getString("gender"))
                            .country(rs.getString("country"))
                            .spokenLanguage(rs.getString("spoken_language"))
                            .build();
                }
            });
            return users.stream().findFirst();
        } catch (DataAccessException e) {
            throw new RuntimeException("데이터베이스 조회 중 오류가 발생했습니다.", e);
        }
    }
}