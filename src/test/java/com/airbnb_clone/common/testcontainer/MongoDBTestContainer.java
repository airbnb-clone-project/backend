package com.airbnb_clone.common.testcontainer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * packageName    : com.airbnb_clone.common.testcontainer
 * fileName       : MongoDBTestContainer
 * author         : ipeac
 * date           : 24. 8. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 29.        ipeac       최초 생성
 */
public class MongoDBTestContainer {
    protected static final DockerImageName MONGO_IMAGE = DockerImageName.parse("mongo:8.0-rc");
    protected static GenericContainer<?> mongodbContainer;

    @BeforeAll
    public static void setUpAll() {
        mongodbContainer = new MongoDBContainer(MONGO_IMAGE)
                .withExposedPorts(27017);
        mongodbContainer.start();
    }

    @AfterAll
    public static void tearDownAll() {
        if (mongodbContainer != null) {
            mongodbContainer.stop();
        }
    }
}
