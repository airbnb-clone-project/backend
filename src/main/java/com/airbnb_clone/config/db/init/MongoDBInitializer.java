package com.airbnb_clone.config.db.init;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

/**
 * packageName    : com.airbnb_clone.config.db.init
 * fileName       : MongoDBInitializer
 * author         : ipeac
 * date           : 24. 8. 15.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 15.        ipeac       최초 생성
 */
@Component
@RequiredArgsConstructor
@Profile("local")
public class MongoDBInitializer {
    private final MongoTemplate mongoTemplate;

    @Bean
    CommandLineRunner initMongoDB() {
        return args -> {
            // 만약 이미 콜렉션이 존재한다면 삭제
            dropIfExists("PIN_TEMPS");

            mongoTemplate.createCollection("PIN_TEMPS");
            mongoTemplate.insert(new Document("user_no", "유저 번호").append("temp_pins", new ArrayList<>()), "PIN_TEMPS");

            // 만약 이미 콜렉션이 존재한다면 삭제
            dropIfExists("CHAT_ROOM");

            mongoTemplate.createCollection("CHAT_ROOM");
            mongoTemplate.insert(new Document("PATICIPANTS", new ArrayList<>())
                                         .append("CREATED_AT", LocalDateTime.now()), "CHAT_ROOM");

            // 만약 이미 콜렉션이 존재한다면 삭제
            dropIfExists("MESSAGE");

            mongoTemplate.createCollection("MESSAGE");
            mongoTemplate.insert(new Document("ID", new org.bson.types.ObjectId())
                                         .append("SENDER_NO", 0L)
                                         .append("CONTENT", "채팅내용")
                                         .append("CREATED_AT", LocalDateTime.now()), "MESSAGE");
        };
    }

    private void dropIfExists(String collectionName) {
        Objects.requireNonNull(collectionName);

        if (mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.dropCollection(collectionName);
        }
    }
}
