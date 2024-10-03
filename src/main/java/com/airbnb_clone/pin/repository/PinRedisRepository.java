package com.airbnb_clone.pin.repository;

import com.airbnb_clone.pin.domain.pin.redis.MainPinHash;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
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
public interface PinRedisRepository extends CrudRepository<MainPinHash, Long>, ListPagingAndSortingRepository<MainPinHash, Long> {
    Page<MainPinHash> findAll(Pageable pageable);

    List<MainPinHash> findMainPinHashByImageClassification(String category, Pageable pageable);
}
