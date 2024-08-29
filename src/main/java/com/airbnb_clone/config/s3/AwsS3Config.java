package com.airbnb_clone.config.s3;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;
import java.time.Duration;

/**
 * packageName    : com.airbnb_clone.config.s3
 * fileName       : S3PropertyHolder
 * author         : ipeac
 * date           : 24. 8. 22.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 22.        ipeac       최초 생성
 */
@Configuration
@Getter
public class AwsS3Config {
    private final static long EXPIRATION_TIME = 3600; // 1시간

    @Value("${spring.cloud.aws.s3.access-key}")
    private String accessKey;

    @Value("${spring.cloud.aws.s3.secret-key}")
    private String secretKey;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${spring.cloud.aws.s3.endpoint}")
    private String endpoint;

    @Value("${spring.cloud.aws.s3.bucket-name}")
    private String bucketName;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsBasicCredentials = getAwsBasicCredentials();

        return S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(() -> awsBasicCredentials)
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true).
                                build())
                .build();
    }

    public AwsBasicCredentials getAwsBasicCredentials() {
        return AwsBasicCredentials.create(accessKey, secretKey);
    }

    public Duration getExpirationTime() {
        return Duration.ofSeconds(EXPIRATION_TIME);
    }
}
