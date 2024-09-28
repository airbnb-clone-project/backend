package com.airbnb_clone.history.repository;

import com.airbnb_clone.history.domain.PersonalHistoryHash;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * packageName    : com.airbnb_clone.history.repository
 * fileName       : PersonalHistoryRedisRepository
 * author         : ipeac
 * date           : 24. 9. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 28.        ipeac       최초 생성
 */
@Repository
public interface PersonalHistoryRedisRepository extends CrudRepository<PersonalHistoryHash, String> {
    List<PersonalHistoryHash> findByUserNoOrderByVisitedAtDesc(Long userNo);
}
