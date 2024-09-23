package com.airbnb_clone.detaile.repository;


import com.airbnb_clone.detaile.dto.DetailPageDto;
import com.airbnb_clone.pin.domain.Pin;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@RequiredArgsConstructor
public class DetailPageRepository {

    private final JdbcTemplate jdbcTemplate;  //jdbc 의존성 주입

    // 특정 핀의 페이지 정보 호출
    public DetailPageDto findPageById(Long pinId) {
        String sql = "SELECT " +
                "p.no AS pinNo, " +
                "p.imgurl, " +
                "p.title, " +
                "p.description, " +
                "p.link, " +
                "u.no AS userNo, " +
                "u.firstname, " +
                "u.lastname, " +
                "u.profileimgurl " +
                "FROM pin p " +
                "JOIN board b ON p.board_no = b.no " +
                "JOIN users u ON b.user_no = u.no " +
                "WHERE p.no = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{pinId}, new RowMapper<DetailPageDto>() {
            @Override
            public DetailPageDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new DetailPageDto(
                        rs.getLong("pinNo"),
                        rs.getString("imgUrl"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("link"),
                        rs.getLong("userNo"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("profileImgUrl")
                );
            }
        });
    }

    //임시메서드
    public Pin save(Pin pin) {
        String sql = "INSERT INTO pin(NO, IMG_URL, TITLE, DESCRIPTION, LINK, BOARD_NO, IS_COMMENT_ALLOWED, LIKE_COUNT)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        jdbcTemplate.update(sql, pin.getNo(), pin.getImgUrl(), pin.getTitle(), pin.getDescription(), pin.getLink(), pin.getBoardNo(), pin.isCommentAllowed(), pin.getLikeCount());
        return pin;

    }
}
