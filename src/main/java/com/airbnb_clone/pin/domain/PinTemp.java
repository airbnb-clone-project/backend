package com.airbnb_clone.pin.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(value = "PIN_TEMPS")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PinTemp {
    @Id
    public ObjectId id;

    @Field(value = "user_no")
    public Long userNo;

    @Field(value = "temp_pins")
    public List<InnerTempPin> innerTempPins = new ArrayList<>();

    public static PinTemp of(Long userNo, List<InnerTempPin> innerTempPins) {
        return PinTemp.builder()
                .userNo(userNo)
                .innerTempPins(innerTempPins)
                .build();
    }
}
