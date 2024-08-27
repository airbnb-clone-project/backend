package com.airbnb_clone.image.service;

import com.airbnb_clone.config.s3.AwsS3Config;
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

    public String generatePresignedUrl(String ImageObjectKey, String contentType) {
        ContentTypeValidator.isValidImageContentType(contentType);

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(awsS3Config.getExpirationTime())
                .putObjectRequest(req -> {
                    req.bucket(awsS3Config.getBucketName());
                    req.key(ImageObjectKey);
                    req.contentType(contentType);
                })
                .build();

        try (S3Presigner presigner = S3Presigner.create()) {
            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

            return presignedRequest.url().toString();
        }
    }
}
