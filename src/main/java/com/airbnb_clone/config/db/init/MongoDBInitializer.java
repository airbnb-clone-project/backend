package com.airbnb_clone.config.db.init;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;

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
public class MongoDBInitializer {
    private final MongoTemplate mongoTemplate;
    
    @Bean
    CommandLineRunner initMongoDB() {
        return args -> {
            // `PIN_TEMPS` 컬렉션이 존재하지 않으면 생성
            if (!mongoTemplate.collectionExists("PIN_TEMPS")) {
                mongoTemplate.createCollection("PIN_TEMPS");
                mongoTemplate.insert(new Document("user_no", "유저 번호").append("temp_pins", new ArrayList<>()), "PIN_TEMPS");
            }
            
            // `TEMP_PIN` 컬렉션이 존재하지 않으면 생성
            if (!mongoTemplate.collectionExists("TEMP_PIN")) {
                mongoTemplate.createCollection("TEMP_PIN");
                mongoTemplate.insert(new Document("IMG_URL", "핀 이미지 URL")
                                             .append("TITLE", "핀 제목")
                                             .append("DESCRIPTION", "핀 설명")
                                             .append("LINK", "링크 URL")
                                             .append("BOARD_NO", 0L)
                                             .append("IS_COMMENT_ALLOWED", true)
                                             .append("created_at", new Date())
                                             .append("updated_at", new Date()), "TEMP_PIN");
            }
            
            // `CHATROOM` 컬렉션이 존재하지 않으면 생성
            if (!mongoTemplate.collectionExists("CHATROOM")) {
                mongoTemplate.createCollection("CHATROOM");
                mongoTemplate.insert(new Document("PATICIPANTS", new ArrayList<>())
                                             .append("CREATED_AT", new Date()), "CHATROOM");
            }
            
            // `MESSAGE` 컬렉션이 존재하지 않으면 생성
            if (!mongoTemplate.collectionExists("MESSAGE")) {
                mongoTemplate.createCollection("MESSAGE");
                mongoTemplate.insert(new Document("CHATROOM_ID", new org.bson.types.ObjectId())
                                             .append("SENDER_NO", 0L)
                                             .append("CONTENT", "채팅내용")
                                             .append("CREATED_AT", new Date()), "MESSAGE");
            }
        };
    }
}