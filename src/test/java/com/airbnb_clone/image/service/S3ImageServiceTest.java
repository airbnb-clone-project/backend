package com.airbnb_clone.image.service;

import com.airbnb_clone.common.testcontainer.LocalStackTestContainer;
import com.airbnb_clone.config.s3.AwsS3Config;
import com.airbnb_clone.exception.image.ContentTypeNotMatchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.localstack.LocalStackContainer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("S3 이미지 서비스 테스트")
public class S3ImageServiceTest extends LocalStackTestContainer {

    private S3ImageService s3ImageService;
    private AwsS3Config s3Config;

    private S3Client s3Client;

    @BeforeEach
    public void setUp() {
        s3Config = new AwsS3Config();

        ReflectionTestUtils.setField(s3Config, "accessKey", "test");
        ReflectionTestUtils.setField(s3Config, "secretKey", "test");
        ReflectionTestUtils.setField(s3Config, "region", "ap-northeast-2");
        ReflectionTestUtils.setField(s3Config, "endpoint", localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3).toString());
        ReflectionTestUtils.setField(s3Config, "bucketName", "test-bucket");

        s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
                .region(Region.of("ap-northeast-2"))
                .endpointOverride(localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();

        s3ImageService = new S3ImageService(s3Config, s3Client);
    }

    @Test
    @DisplayName("S3 이미지 URL이 정상으로 생성되는 경우")
    public void generatePresignedUrl() {
        // given
        String contentType = "image/png";

        // when
        String actualPreSignedUrl = s3ImageService.generatePresignedUrl(contentType);

        // then
        assertThat(actualPreSignedUrl).isNotNull();
        assertThat(actualPreSignedUrl).contains("test-bucket");
    }

    @Test
    @DisplayName("S3 이미지 URL이 생성되지 않는 경우 - 컨텐츠 타입 불일치")
    public void generatePresignedUrlFail() {
        // given
        String contentType = "image/jpg";

        // when
        // then
        assertThatThrownBy(() -> s3ImageService.generatePresignedUrl( contentType))
                .isInstanceOf(ContentTypeNotMatchException.class)
                .hasMessage("지원하지 않는 컨텐츠 타입입니다.");
    }
}
