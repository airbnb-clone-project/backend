package com.airbnb_clone.pin.domain;

import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.pin.PinNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Document(value = "PIN_TEMPS")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PinTemp {
    @Id
    private ObjectId id;

    @Field(value = "user_no")
    private Long userNo;

    @Field(value = "temp_pins")
    @Indexed
    private Set<InnerTempPin> innerTempPins = new LinkedHashSet<>();

    public static PinTemp of(Long userNo, Set<InnerTempPin> innerTempPins) {
        return PinTemp.builder()
                .userNo(userNo)
                .innerTempPins(innerTempPins)
                .build();
    }

    public InnerTempPin getInnerTempPinById(ObjectId id) {
        return innerTempPins.stream()
                .filter(innerTempPin -> Objects.equals(innerTempPin.get_id(), id))
                .findFirst()
                .orElseThrow(() -> new PinNotFoundException(ErrorCode.PIN_NOT_FOUND));
    }
}
