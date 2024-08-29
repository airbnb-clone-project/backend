package com.airbnb_clone.common.testcontainer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * packageName    : com.airbnb_clone.common.testcontainer
 * fileName       : LocalStackTestContainer
 * author         : ipeac
 * date           : 24. 8. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 29.        ipeac       최초 생성
 */
public class LocalStackTestContainer {
    protected static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse("localstack/localstack:3.6");
    protected static LocalStackContainer localStackContainer;

    @BeforeAll
    public static void setUpAll() {
        localStackContainer = new LocalStackContainer(LOCALSTACK_IMAGE)
                .withServices(LocalStackContainer.Service.S3);
        localStackContainer.start();
    }

    @AfterAll
    public static void tearDownAll() {
        if (localStackContainer != null) {
            localStackContainer.stop();
        }
    }
}
