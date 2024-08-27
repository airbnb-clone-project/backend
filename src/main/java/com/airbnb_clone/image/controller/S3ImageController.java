package com.airbnb_clone.image.controller;

import com.airbnb_clone.common.global.response.ApiResponse;
import com.airbnb_clone.image.service.S3ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName    : com.airbnb_clone.image.controller
 * fileName       : S3ImageController
 * author         : ipeac
 * date           : 24. 8. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 24.        ipeac       최초 생성
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/s3/images")
public class S3ImageController {
    private final S3ImageService s3ImageService;

    /**
     * Generate presigned url for image upload
     *
     * @param contentType image content type
     * @return presigned url
     */
    @GetMapping("/pre-signed-url/v1")
    public ApiResponse<String> getImage(@RequestParam String contentType) {
        return ApiResponse.of("Presined URL generated successfully", HttpStatus.OK.value(), s3ImageService.generatePresignedUrl(MediaType.valueOf(contentType).toString()));
    }
}
