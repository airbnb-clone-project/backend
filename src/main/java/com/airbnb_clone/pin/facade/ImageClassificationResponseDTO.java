package com.airbnb_clone.pin.facade;

import com.airbnb_clone.image.enums.ImageClassificationEnum;
import lombok.*;

/**
 * packageName    : com.airbnb_clone.pin.facade
 * fileName       : ImageClassificationResponseDTO
 * author         : ipeac
 * date           : 24. 9. 22.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 22.        ipeac       최초 생성
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class ImageClassificationResponseDTO {
    private String imageUrl;
    private ImageClassificationEnum imageCategory;

    public static ImageClassificationResponseDTO of(String imageUrl, ImageClassificationEnum imageCategory) {
        return ImageClassificationResponseDTO.builder()
                .imageUrl(imageUrl)
                .imageCategory(imageCategory)
                .build();
    }
}
