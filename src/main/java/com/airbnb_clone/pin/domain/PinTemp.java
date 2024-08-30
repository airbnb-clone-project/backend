package com.airbnb_clone.pin.domain;

import com.airbnb_clone.pin.domain.dto.response.TemporaryPinDetailResponseDTO;
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
    private ObjectId id;

    @Field(value = "user_no")
    private Long userNo;

    @Field(value = "temp_pins")
    private List<InnerTempPin> innerTempPins = new ArrayList<>();

    public static PinTemp of(Long userNo, List<InnerTempPin> innerTempPins) {
        return PinTemp.builder()
                .userNo(userNo)
                .innerTempPins(innerTempPins)
                .build();
    }

    public TemporaryPinDetailResponseDTO toTemporaryPinDetailResponseDTO() {
        return TemporaryPinDetailResponseDTO.builder()
                .tempPinNo(this.id.toHexString())
                .userNo(this.userNo)
                .innerTempPins(this.innerTempPins)
                .build();
    }
}
