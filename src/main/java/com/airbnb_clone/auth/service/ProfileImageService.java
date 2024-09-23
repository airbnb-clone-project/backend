package com.airbnb_clone.auth.service;

import com.airbnb_clone.auth.domain.Users;
import com.airbnb_clone.auth.dto.ErrorResponse;
import com.airbnb_clone.auth.repository.UserRepository;
import com.airbnb_clone.config.s3.AwsS3Config;
import com.airbnb_clone.image.helper.S3UniqueKeyGenerator;
import com.airbnb_clone.image.validator.ContentTypeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * packageName    : com.airbnb_clone.auth.service
 * fileName       : ProfileImageService
 * author         : doungukkim
 * date           : 2024. 9. 20.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024. 9. 20.        doungukkim       최초 생성
 */
@Service
@RequiredArgsConstructor
public class ProfileImageService {
    private final AwsS3Config awsS3Config;
    private final S3Client s3Client;
    private final UserRepository userRepository;

    // S3 에 이미지를 업로드 할 수 있는 사전 서명된 URL을 생성
    public String generatePresignedUrl(String contentType) {
        ContentTypeValidator.isValidImageContentType(contentType);
        // S3Presigner : AWS 자격 증명 없이 S3 버킷에 대해 특정 작업(파일 업로드, 다운로드) 수행 가능
        try (S3Presigner presigner = S3Presigner.builder()
                // AWS 서비스에 접근하기 위한 인증 정보 제공
                .credentialsProvider(awsS3Config::getAwsBasicCredentials)
                // S3 버킷이 위치한 AWS 리전 설정
                .region(awsS3Config.getRegion())
                .build()) {
            String uniqueKey = S3UniqueKeyGenerator.generateUniqueKey();
            PutObjectPresignRequest presignRequest = getPutObjectPresignRequest(contentType, uniqueKey);

            PresignedPutObjectRequest presignedPutObjectRequest = presigner.presignPutObject(presignRequest);
            return presignedPutObjectRequest.url().toString();
        }
    }

    // 사용자의 프로필 이미지 URL을 저장
    public ResponseEntity<?> saveProfileImageMetadata(String imageUrl) {
        // userId는 토큰에서 가져온다.
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // username과 일치하는 username 있는지 확인
        if (userRepository.isUsernameNotExist(username)) {
            ErrorResponse errorResponse = new ErrorResponse(401, "일치하는 유저 정보가 없습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }

        userRepository.updateUserProfileImage(username,imageUrl);

        ErrorResponse errorResponse = new ErrorResponse(200, "이미지가 업데이트 되었습니다.");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(errorResponse);
    }

    public ResponseEntity<?> getProfileImage() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // 유저 확인
        if (userRepository.isUsernameNotExist(username)) {
            ErrorResponse errorResponse = new ErrorResponse(401, "일치하는 유저 정보가 없습니다.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }
        // profile img url 확인
        String imageUrl = userRepository.findProfileImageByUsername(username);
        if (imageUrl == null) {
            ErrorResponse errorResponse = new ErrorResponse(401, "유저 프로필이 없습니다..");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(errorResponse);
        }
        // 응답
        Map<String, Object> json = new HashMap<>();
        json.put("profileImgUrl", imageUrl);
        json.put("status", 200);
        json.put("message", "이미지를 불러왔습니다.");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(json);
    }

    // url에서 key 추출
    private String extractKeyFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    // S3에 객체를 업로드하기 위한 사전 서명된 URL을 생성하는 데 필요한 요청 객체 생성
    private PutObjectPresignRequest getPutObjectPresignRequest(String contentType, String uniqueKey) {
        return PutObjectPresignRequest.builder()
                .signatureDuration(awsS3Config.getExpirationTime())
                .putObjectRequest(req -> {
                    req.bucket(awsS3Config.getBucketName());
                    req.key(uniqueKey);
                    req.contentType(contentType);
                })
                .build();
    }

}
