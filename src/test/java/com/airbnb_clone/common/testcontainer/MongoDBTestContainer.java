package com.airbnb_clone.common.testcontainer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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
    protected static MongoDBContainer  mongodbContainer;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> mongodbContainer.getReplicaSetUrl("embedded"));
    }

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
