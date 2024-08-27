package com.airbnb_clone.image.service;

import com.airbnb_clone.config.s3.AwsS3Config;
import com.airbnb_clone.exception.image.ContentTypeNotMatchException;
import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class S3ImageServiceTest {

    private static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse("localstack/localstack:3.6");

    private static LocalStackContainer localStackContainer;

    private S3ImageService s3ImageService;
    private AwsS3Config s3Config;

    @BeforeAll
    public static void setUpAll() {
        localStackContainer = new LocalStackContainer(LOCALSTACK_IMAGE)
                .withServices(LocalStackContainer.Service.S3);
        localStackContainer.start();
    }

    @BeforeEach
    public void setUp() {
        s3Config = new AwsS3Config();
        ReflectionTestUtils.setField(s3Config, "accessKey", "test");
        ReflectionTestUtils.setField(s3Config, "secretKey", "test");
        ReflectionTestUtils.setField(s3Config, "region", "ap-northeast-2");
        ReflectionTestUtils.setField(s3Config, "endpoint", localStackContainer.getEndpointOverride(LocalStackContainer.Service.S3).toString());
        ReflectionTestUtils.setField(s3Config, "bucketName", "test-bucket");

        s3ImageService = new S3ImageService(s3Config);
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

    @AfterAll
    public static void tearDownAll() {
        if (localStackContainer != null) {
            localStackContainer.stop();
        }
    }
}
