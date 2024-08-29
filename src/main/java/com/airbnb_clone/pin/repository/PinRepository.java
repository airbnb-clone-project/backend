package com.airbnb_clone.pin.repository;

import com.airbnb_clone.pin.domain.PinTemp;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * packageName    : com.airbnb_clone.pin.repository
 * fileName       : PinRepository
 * author         : ipeac
 * date           : 24. 8. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 29.        ipeac       최초 생성
 */
@Repository
@RequiredArgsConstructor
public class PinRepository {
    private final MongoTemplate mt;

    public Optional<PinTemp> findPinTempByUserNo(@NotNull Long userNo) {
        return Optional.ofNullable(mt.findById(userNo, PinTemp.class));
    }
}
