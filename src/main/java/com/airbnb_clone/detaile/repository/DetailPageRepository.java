package com.airbnb_clone.detaile.repository;


import com.airbnb_clone.detaile.dto.DetailPageDto;
import com.airbnb_clone.detaile.dto.PinLikeDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DetailPageRepository {

    private final JdbcTemplate jdbcTemplate;  //jdbc 의존성 주입

    // 특정 핀의 페이지 정보 호출
    public DetailPageDto findPageById(Long pinId) {
        String sql = "SELECT " +
                "p.no AS pinNo, " +
                "p.img_url, " +
                "p.title, " +
                "p.description, " +
                "p.link, " +
                "u.no AS userNo, " +
                "u.first_name, " +
                "u.last_name, " +
                "u.profile_img_url " +
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
                        rs.getString("first_Name"),
                        rs.getString("last_Name"),
                        rs.getString("profile_img_url")
                );
            }
        });
    }



    //특정 핀의 핀 반응 목록 호출
    public List<PinLikeDto> findPinlikesById(Long pinId){
        String sql = "SELECT " +
                "pl.no AS pinLikeNo, " +
                "pl.target_pin_no, " +
                "pl.emoji_no " +
                "pl.created_at " +
                "pl.liker " +
                "u.first_name, " +
                "u.last_name, " +
                "u.profile_img_url " +
                "FROM pin_like pl " +
                "JOIN pin p ON pl.target_pin_no = p.no " +
                "JOIN users u ON pl.liker = u.no " +
                "WHERE p.no = ? " +
                "ORDER BY pl.created_at DESC ";

        return jdbcTemplate.query(sql, new Object[]{pinId}, new RowMapper<PinLikeDto>() {
                    @Override
                    public PinLikeDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Instant createdAt = rs.getTimestamp("createdAt").toInstant();

                        return new PinLikeDto(
                                rs.getLong("pinLikeNo"),
                                rs.getLong("target_pin_no"),
                                rs.getInt("emoji_no"),
                                rs.getLong("liker"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("profile_img_url"),
                                createdAt
                                );
                    }
        });
    }
}






//    //임시메서드
//    public Pin save(Pin pin) {
//        String sql = "INSERT INTO pin(NO, IMG_URL, TITLE, DESCRIPTION, LINK, BOARD_NO, IS_COMMENT_ALLOWED, LIKE_COUNT)" +
//                "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
//        jdbcTemplate.update(sql, pin.getNo(), pin.getImgUrl(), pin.getTitle(), pin.getDescription(), pin.getLink(), pin.getBoardNo(), pin.isCommentAllowed(), pin.getLikeCount());
//        return pin;
//
//    }
//}
