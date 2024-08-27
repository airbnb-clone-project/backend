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

    @GetMapping("/pre-signed-url/v1")
    public ApiResponse<String> getImage(@RequestParam String contentType) {
        s3ImageService.generatePresignedUrl(MediaType.valueOf(contentType).toString());
        return ApiResponse.of("이미지 URL을 가져오는 API입니다", HttpStatus.OK.value(), "success");
    }
}
