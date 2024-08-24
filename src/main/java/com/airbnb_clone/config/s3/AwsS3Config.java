package com.airbnb_clone.config.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

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
    @Value("${AWS_ACCESS_KEY}")
    private String accessKey;

    @Value("${AWS_SECRET_KEY}")
    private String secretKey;

    /*@Bean
    public S3Client s3Client() {
        //AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create();
        return null;
    }*/
}
