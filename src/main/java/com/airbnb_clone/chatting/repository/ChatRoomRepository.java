package com.airbnb_clone.chatting.repository;

import com.airbnb_clone.chatting.domain.ChatRoom;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewReqDto;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {

    private final MongoTemplate mt;

    /**
     *
     * ChatRoom을 인자로 받아 몽고에 저장하고, 채팅방의 키 값을 반환합니다.
     *
     * @param chatRoom 채팅방 정보
     * @return chatRoom
     */
    public ObjectId save(ChatRoom chatRoom) {
        mt.insert(chatRoom);

        return chatRoom.getNo();
    }

    /**
     *  채팅방의 키 값을 받아, 채팅방을 찾습니다.
     *
     * @param id 채팅방의 키 값
     * @return Optional&lt;ChatRoom&gt;
     */
    public Optional<ChatRoom> findById(ObjectId id) {
        return Optional.ofNullable(mt.findById(id, ChatRoom.class));
    }

    /**
     *
     * 두 명의 유저 아이디를 받아 기존 채팅방이 존재하는 지 확인합니다.
     *
     * @param id1
     * @param id2
     * @return Optional&lt;ChatRoom&gt;
     */
    public Optional<ChatRoom> checkExistingChatRoom(Integer id1, Integer id2) {
        Query query = new Query(Criteria.where("participants").all(id1, id2));

        return Optional.ofNullable(mt.findOne(query, ChatRoom.class));
    }
}
