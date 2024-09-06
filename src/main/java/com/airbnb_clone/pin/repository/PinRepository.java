package com.airbnb_clone.pin.repository;

import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.pin.PinNotFoundException;
import com.airbnb_clone.pin.domain.InnerTempPin;
import com.airbnb_clone.pin.domain.PinTemp;
import com.airbnb_clone.pin.domain.dto.request.TemporaryPinUpdateRequestDTO;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
        Query query = new Query(Criteria.where("user_no").is(userNo));
        return Optional.ofNullable(mt.findOne(query, PinTemp.class));
    }

    public Optional<InnerTempPin> findInnerTempPinById(@NotNull ObjectId id) {
        Query query = new Query(Criteria.where("temp_pins").elemMatch(Criteria.where("_id").is(id)));
        query.fields().include("temp_pins.$");

        PinTemp result = Optional.ofNullable(mt.findOne(query, PinTemp.class)).orElseThrow(() -> new PinNotFoundException(ErrorCode.PIN_NOT_FOUND));

        return Optional.ofNullable(result.getInnerTempPinById(id));
    }

    public ObjectId saveAndGetPinId(PinTemp pinTemp) {
        mt.save(pinTemp);
        return pinTemp.getId();
    }

    public ObjectId addInnerTempPinAndGetTempPinId(@NotNull Long userNo, @NotNull String imgUrl) {
        Query query = new Query(Criteria.where("user_no").is(userNo));

        InnerTempPin insertInnerTempPin = InnerTempPin.of(imgUrl);

        Update update = new Update().push("temp_pins", insertInnerTempPin);

        mt.updateFirst(query, update, PinTemp.class);

        return insertInnerTempPin.get_id();
    }

    public void updateInnerTempPin(ObjectId tempPinNo, TemporaryPinUpdateRequestDTO temporaryPinUpdateRequestDTO) {
        Query query = new Query(Criteria.where("temp_pins._id").is(tempPinNo));

        Update update = new Update()
                .set("innerTempPins.$.boardNo", temporaryPinUpdateRequestDTO.getBoardNo())
                .set("innerTempPins.$.description", temporaryPinUpdateRequestDTO.getDescription())
                .set("innerTempPins.$.title", temporaryPinUpdateRequestDTO.getTitle())
                .set("innerTempPins.$.isCommentAllowed", temporaryPinUpdateRequestDTO.isCommentAllowed())
                .set("innerTempPins.$.link", temporaryPinUpdateRequestDTO.getLink())
                .set("innerTempPins.$.updatedAt", LocalDateTime.now());

        // 업데이트 실행
        mt.updateFirst(query, update, PinTemp.class);
    }
}
