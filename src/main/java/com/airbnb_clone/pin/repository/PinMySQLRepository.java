package com.airbnb_clone.pin.repository;

import com.airbnb_clone.pin.domain.Pin;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.Objects;

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
    private final JdbcTemplate jt;

    public Long savePinAndGetId(Pin savePin) {
        String sql = "INSERT INTO pin (USER_NO, TITLE, DESCRIPTION, IMG_URL, LINK, BOARD_NO, IS_COMMENT_ALLOWED, CREATED_AT, UPDATED_AT) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        PreparedStatementCreator savePstmt = new PreparedStatementCreator() {
            @Override
            public @NotNull PreparedStatement createPreparedStatement(@NotNull Connection con) throws SQLException {
                PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                pstmt.setLong(1, savePin.getUserNo());
                pstmt.setString(2, savePin.getImgUrl());
                pstmt.setString(3, savePin.getTitle());
                pstmt.setString(4, savePin.getDescription());
                pstmt.setString(5, savePin.getLink());
                pstmt.setLong(6, savePin.getBoardNo());
                pstmt.setBoolean(7, savePin.isCommentAllowed());

                pstmt.setTimestamp(8, Timestamp.valueOf(savePin.getCreatedAt()));
                pstmt.setTimestamp(9, Timestamp.valueOf(savePin.getUpdatedAt()));

                return pstmt;
            }
        };

        jt.update(sql, savePstmt, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }
}
