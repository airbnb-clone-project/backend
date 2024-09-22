package com.airbnb_clone.image.service;

import com.airbnb_clone.config.s3.AwsS3Config;
import com.airbnb_clone.image.helper.S3UniqueKeyGenerator;
import com.airbnb_clone.image.validator.ContentTypeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;

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
@Slf4j
@RequiredArgsConstructor
public class S3ImageService {
    private final AwsS3Config awsS3Config;
    private final S3Client s3Client;

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

    public String uploadImage(MultipartFile imageFile) {
        ContentTypeValidator.isValidImageContentType(imageFile.getContentType());

        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(imageFile.getOriginalFilename(), "-" + System.currentTimeMillis());
            Files.copy(imageFile.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to create temp file : {}", e.getMessage());
        }

        //S3 업로드
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsS3Config.getBucketName())
                .key(S3UniqueKeyGenerator.generateUniqueKey())
                .contentType(imageFile.getContentType())
                .build();

        try{
            PutObjectResponse response = s3Client.putObject(putObjectRequest, tempFile);
            if (!response.sdkHttpResponse().isSuccessful()) {
                throw new RuntimeException("Failed to upload image to S3");
            }
        }catch (RuntimeException e){
            log.error("Failed to upload image to S3 : {}", e.getMessage());
        }finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException e) {
                    log.error("Failed to delete temp file : {}", e.getMessage());
                }
            }
        }

        return MessageFormat.format("https://{0}.s3.{1}.amazonaws.com/{2}", awsS3Config.getBucketName(), awsS3Config.getRegion().id(), putObjectRequest.key());
    }
}
