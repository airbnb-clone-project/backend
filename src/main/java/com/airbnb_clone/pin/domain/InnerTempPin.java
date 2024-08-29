package com.airbnb_clone.pin.domain;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class InnerTempPin {
    @Id
    public ObjectId id;

    @Field(value = "BOARD_NO")
    public int boardNo;

    @Field(value = "DESCRIPTION")
    public String description;

    @Field(value = "TITLE")
    public String title;

    @Field(value = "IMG_URL")
    public String imgUrl;

    @Field(value = "IS_COMMENT_ALLOWED")
    public Boolean isCommentAllowed;

    @Field(value = "LINK")
    public String link;

    @Field(value = "CREATED_AT")
    @CreatedDate
    public LocalDateTime createdAt;

    @Field(value = "UPDATED_AT")
    public LocalDateTime updatedAt;

    public static InnerTempPin of(@NotNull String imgUrl) {
        return InnerTempPin.builder()
                .imgUrl(imgUrl)
                .build();
    }
}
