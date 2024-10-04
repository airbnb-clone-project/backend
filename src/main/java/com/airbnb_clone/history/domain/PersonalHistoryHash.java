package com.airbnb_clone.history.domain;

import com.airbnb_clone.pin.domain.pin.dto.response.PinHistoryResponseDTO;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;

/**
 * packageName    : com.airbnb_clone.history.domain
 * fileName       : PersonalHistoryHash
 * author         : ipeac
 * date           : 24. 9. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 28.        ipeac       최초 생성
 */
@RedisHash("personal_history")
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class PersonalHistoryHash {
    @Id
    private String id;
    @Indexed
    private Long userNo;
    private String visitedHistoryClassification;
    private LocalDateTime visitedAt;

    public static PersonalHistoryHash from(@NonNull PinHistoryResponseDTO foundPin, @NonNull Long userNo) {
        return PersonalHistoryHash.builder()
                .userNo(userNo)
                .visitedHistoryClassification(foundPin.getImageClassification())
                .visitedAt(LocalDateTime.now())
                .build();
    }

    public static PersonalHistoryHash of(Long userNo, String visitedHistoryClassification) {
        return PersonalHistoryHash.builder()
                .userNo(userNo)
                .visitedHistoryClassification(visitedHistoryClassification)
                .visitedAt(LocalDateTime.now())
                .build();
    }

}
