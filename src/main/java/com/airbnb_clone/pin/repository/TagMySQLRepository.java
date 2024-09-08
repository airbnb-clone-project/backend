package com.airbnb_clone.pin.repository;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

/**
 * packageName    : com.airbnb_clone.pin.repository
 * fileName       : TagMySQLRepository
 * author         : ipeac
 * date           : 24. 9. 8.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 8.        ipeac       최초 생성
 */
@Repository
@RequiredArgsConstructor
public class TagMySQLRepository {
    private final NamedParameterJdbcTemplate npjt;

    public boolean existsTagByNoIn(@NotNull Set<Long> tagNos) {
        if(tagNos.isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(NO) FROM TAG WHERE NO IN (:tagNos)";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("tagNos", tagNos);

        int count = Optional.ofNullable(npjt.queryForObject(sql, parameters, Integer.class)).orElseGet(() -> 0);

        return count == tagNos.size();
    }

    public void savePinTags(Long pinNo, Set<Long> tagNos) {
        String sql = "INSERT INTO PIN_TAG (PIN_NO, TAG_NO) VALUES (:pinNo, :tagNo)";

        MapSqlParameterSource[] batchArgs = tagNos.stream()
                .map(tagNo -> new MapSqlParameterSource()
                        .addValue("pinNo", pinNo)
                        .addValue("tagNo", tagNo))
                .toArray(MapSqlParameterSource[]::new);

        npjt.batchUpdate(sql, batchArgs);
    }
}
