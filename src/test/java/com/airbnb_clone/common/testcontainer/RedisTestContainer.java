package com.airbnb_clone.common.testcontainer;

import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
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
public class RedisTestContainer {
    protected static final DockerImageName REDIS_IMAGE = DockerImageName.parse("redis:7.4.0-alpine");
    protected static RedisContainer redisContainer;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getRedisHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }

    @BeforeAll
    public static void setUpAll() {
        redisContainer = new RedisContainer(REDIS_IMAGE);
        redisContainer.start();
    }

    @AfterAll
    public static void tearDownAll() {
        if (redisContainer != null) {
            redisContainer.stop();
        }
    }
}
