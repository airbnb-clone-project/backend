package com.airbnb_clone.config.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.services.s3.S3Client;

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
@Profile(value = {"local"})
@Configuration
public class AwsS3Config {
    @Value("${aws.s3.accessKey}")
    private String accessKey;

    @Value("${aws.s3.secretKey}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        //AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create();
        return null;
    }
}
