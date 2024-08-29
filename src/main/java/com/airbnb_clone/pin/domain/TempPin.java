package com.airbnb_clone.pin.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class TempPin {
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
    public LocalDateTime createdAt;

    @Field(value = "UPDATED_AT")
    public LocalDateTime updatedAt;
}
