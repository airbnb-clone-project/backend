package com.airbnb_clone.pin.repository;

import com.airbnb_clone.pin.domain.InnerTempPin;
import com.airbnb_clone.pin.domain.PinTemp;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
        Query query = new Query(Criteria.where("user_no").is(userNo));
        return Optional.ofNullable(mt.findOne(query, PinTemp.class));
    }

    public Optional<InnerTempPin> findInnerTempPinById(@NotNull ObjectId id) {
        Query query = new Query(Criteria.where("temp_pins._id").is(id));

        return Optional.ofNullable(mt.findOne(query, PinTemp.class))
                .map(pinTemp -> pinTemp.getInnerTempPinById(id));
    }

    public ObjectId saveAndGetId(PinTemp pinTemp) {
        mt.save(pinTemp);
        return pinTemp.getId();
    }

    public ObjectId addInnerTempPinAndGetTempPinId(@NotNull Long userNo, @NotNull String imgUrl) {
        Query query = new Query(Criteria.where("user_no").is(userNo));

        Update update = new Update().push("temp_pins", InnerTempPin.of(imgUrl));

        mt.updateFirst(query, update, PinTemp.class);

        PinTemp updatedPinTemp = mt.findOne(query, PinTemp.class);

        return updatedPinTemp != null ? updatedPinTemp.getId() : null;
    }
}
