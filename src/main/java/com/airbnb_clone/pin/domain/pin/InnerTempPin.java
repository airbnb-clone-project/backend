package com.airbnb_clone.pin.domain.pin;

import com.airbnb_clone.image.enums.ImageClassificationEnum;
import com.airbnb_clone.pin.domain.pin.dto.response.TemporaryPinDetailResponseDTO;
import com.airbnb_clone.pin.domain.pin.dto.response.TemporaryPinsResponseDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@Document(value = "INNER_TEMP_PINS")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class InnerTempPin {
    @Id
    public ObjectId _id;

    @Field(value = "BOARD_NO")
    public Integer boardNo;

    @Field(value = "DESCRIPTION")
    public String description;

    @Field(value = "TITLE")
    public String title;

    @Field(value = "IMG_URL")
    public String imgUrl;

    @Field(value = "IS_COMMENT_ALLOWED")
    public boolean isCommentAllowed;

    @Field(value = "LINK")
    public String link;

    @Field(value =  "IMAGE_CLASSIFICATION")
    public String imageClassification;

    @Field(value = "CREATED_AT")
    @CreatedDate
    public LocalDateTime createdAt;

    @Field(value = "UPDATED_AT")
    public LocalDateTime updatedAt;

    public InnerTempPin() {
        _id = new ObjectId();
    }

    public static InnerTempPin of(@NotNull String imgUrl, @NotNull ImageClassificationEnum imageClassification) {
        return InnerTempPin.builder()
                ._id(new ObjectId())
                .imgUrl(imgUrl)
                .imageClassification(imageClassification.getKoreanName())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public TemporaryPinDetailResponseDTO toTemporaryPinDetailResponseDTO() {
        return TemporaryPinDetailResponseDTO.builder()
                .title(title)
                .description(description)
                .imgUrl(imgUrl)
                .link(link)
                .isCommentAllowed(isCommentAllowed)
                .boardNo(boardNo)
                .imageClassification(imageClassification)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    public TemporaryPinsResponseDTO toTemporaryPinsResponseDTO() {
        return TemporaryPinsResponseDTO.builder()
                .tempPinNo(String.valueOf(_id))
                .title(title)
                .description(description)
                .imgUrl(imgUrl)
                .link(link)
                .isCommentAllowed(isCommentAllowed)
                .boardNo(boardNo)
                .imageClassification(imageClassification)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
