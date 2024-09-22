package com.airbnb_clone.image.enums;

import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.image.ImageClassficationEnumNotFoundException;
import lombok.Getter;

import java.util.Objects;

/**
 * packageName    : com.airbnb_clone.image.enums
 * fileName       : ImageClassificationEnum
 * author         : ipeac
 * date           : 24. 9. 22.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 22.        ipeac       최초 생성
 */
@Getter
public enum ImageClassificationEnum {
    ART("예술"),

    FOOD("음식"),

    INTERIOR("인테리어"),

    NATURE("자연"),

    NIGHT_VIEW("야경"),

    PEOPLE("인물");

    private final String koreanName;

    ImageClassificationEnum(String koreanName) {
        this.koreanName = koreanName;
    }

    public static ImageClassificationEnum fromKoreanName(String koreanName) {
        for (ImageClassificationEnum imageClassificationEnum : values()) {
            if (Objects.equals(imageClassificationEnum.koreanName, koreanName)) {
                return imageClassificationEnum;
            }
        }

        throw new ImageClassficationEnumNotFoundException(ErrorCode.IMAGE_CLASSIFICATION_ENUM_NOT_FOUND);
    }
}
