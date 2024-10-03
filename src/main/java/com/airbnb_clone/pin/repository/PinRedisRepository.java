package com.airbnb_clone.pin.repository;

import com.airbnb_clone.pin.domain.pin.dto.response.PinMainResponseDTO;
import com.airbnb_clone.pin.domain.pin.redis.MainPinHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * packageName    : com.airbnb_clone.pin.repository
 * fileName       : PinRedisRepository
 * author         : ipeac
 * date           : 24. 9. 10.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 10.        ipeac       최초 생성
 */
@Repository
public interface PinRedisRepository extends CrudRepository<MainPinHash, Long> {
    List<PinMainResponseDTO> findAllPins(int limitPerCategory);

    List<MainPinHash> findPinsByImageClassification(String category, int offset, int limit);
}
