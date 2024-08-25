package com.airbnb_clone.image.service;

import com.airbnb_clone.common.annotation.DataJdbcTestAnnotation;
import com.airbnb_clone.config.s3.AwsS3Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import static org.assertj.core.api.Assertions.assertThat;

@DataJdbcTestAnnotation
public class S3ImageServiceTest {

    private S3ImageService s3ImageService;

    @MockBean
    private AwsS3Config s3Config;

    @MockBean
    private S3Presigner s3Presigner;

    @BeforeEach
    public void setUp() {
        s3ImageService = new S3ImageService(s3Config, s3Presigner);
    }

    @Nested
    public class PresignedUrlTest {
        @Test
        @DisplayName("S3 이미지 URL이 정상으로 생성되는 경우")
        public void generatePresignedUrl() {
            // given

            // when
            String actualPreSignedUrl = s3ImageService.generatePresignedUrl("test", "image/png");

            // then
            assertThat(actualPreSignedUrl).isEqualTo("https://test.com");
        }
    }
}
