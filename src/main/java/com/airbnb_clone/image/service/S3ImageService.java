package com.airbnb_clone.image.service;

import com.airbnb_clone.config.s3.AwsS3Config;
import com.airbnb_clone.image.validator.ContentTypeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.function.Consumer;

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
    private final AwsS3Config s3Config;
    private final S3Presigner s3Presigner;

    public String generatePresignedUrl(String ImageObjectKey, String contentType) {
        ContentTypeValidator.isValidImageContentType(contentType);

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(s3Config.getExpirationTime())
                .putObjectRequest(req -> req.bucket(s3Config.getBucketName()).key(ImageObjectKey).contentType(contentType))
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        return presignedRequest.url().toString();
    }
}
