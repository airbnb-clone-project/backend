package com.airbnb_clone.auth.service;

import com.airbnb_clone.auth.domain.Users;
import com.airbnb_clone.config.s3.AwsS3Config;
import com.airbnb_clone.image.validator.ContentTypeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

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
    private final ProfileImageRepository profileImageRepository;
    private final S3Client s3Client;

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

        }
    }

    // 사용자의 프로필 이미지 URL을 저장
    public Users saveProfileImageMetadata(String imageUrl) {
        // userId는 토큰에서 가져온다.
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
