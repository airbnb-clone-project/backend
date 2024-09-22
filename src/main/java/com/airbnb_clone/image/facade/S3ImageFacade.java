package com.airbnb_clone.image.facade;

import com.airbnb_clone.image.dto.response.ImageClassificationResponseDTO;
import com.airbnb_clone.image.enums.ImageClassificationEnum;
import com.airbnb_clone.image.service.ExternalApiService;
import com.airbnb_clone.image.service.S3ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

/**
 * packageName    : com.airbnb_clone.pin.facade
 * fileName       : S3ImageFacade
 * author         : ipeac
 * date           : 24. 9. 22.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 22.        ipeac       최초 생성
 */
@Component
@RequiredArgsConstructor
public class S3ImageFacade {
    private final S3ImageService s3ImageService;
    private final ExternalApiService externalApiService;

    /**
     *  S3 에 이미지 업로드 후 이미지 분류 요청 처리
     *
     * @param imageFile image file
     * @return
     */
    public Mono<ImageClassificationResponseDTO> uploadAndClassifyImage(MultipartFile imageFile) {
        String imageUrl = s3ImageService.uploadImage(imageFile);
        Mono<ImageClassificationEnum> imageCategory = externalApiService.classifyImage(imageFile);

        return imageCategory.map(category -> ImageClassificationResponseDTO.of(imageUrl, category));
    }
}
