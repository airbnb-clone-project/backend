package com.airbnb_clone.pin.domain.pin;

import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.pin.PinNotFoundException;
import lombok.*;
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

    @Field(value = "user_email")
    @Indexed
    private String userEmail;

    @Field(value = "temp_pins")
    @Builder.Default
    private Set<InnerTempPin> innerTempPins = new LinkedHashSet<>();

    public boolean isOwner(@NonNull String userEmail, @NonNull ObjectId tempPinId) {
        return Objects.equals(this.userEmail, userEmail) && innerTempPins.stream()
                .anyMatch(innerTempPin -> Objects.equals(innerTempPin.get_id(), tempPinId));
    }
    
    public static PinTemp of(String userEmail, Set<InnerTempPin> innerTempPins) {
        return PinTemp.builder()
                .userEmail(userEmail)
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
