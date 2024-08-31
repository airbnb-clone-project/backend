package com.airbnb_clone.image.service;

import com.airbnb_clone.config.s3.AwsS3Config;
import com.airbnb_clone.image.helper.S3UniqueKeyGenerator;
import com.airbnb_clone.image.validator.ContentTypeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

/**
 * packageName    : com.airbnb_clone.image.service
 * fileName       : S3ImageService
 * author         : ipeac
 * date           : 24. 8. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 25.        ipeac       최초 생성
 */
@Service
@RequiredArgsConstructor
public class S3ImageService {
    private final AwsS3Config awsS3Config;

    public String generatePresignedUrl(String contentType) {
        ContentTypeValidator.isValidImageContentType(contentType);

        try (S3Presigner presigner = S3Presigner.builder()
                .credentialsProvider(awsS3Config::getAwsBasicCredentials)
                .region(awsS3Config.getRegion())
                .build()) {
            PutObjectPresignRequest presignRequest = getPutObjectPresignRequest(contentType, S3UniqueKeyGenerator.generateUniqueKey());

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

            return presignedRequest.url().toString();
        }
    }

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
