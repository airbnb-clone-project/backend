package com.airbnb_clone.chatting.repository.Dto.chatRoom;

import com.airbnb_clone.chatting.domain.ChatRoom;
import lombok.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static lombok.AccessLevel.*;

@AllArgsConstructor(access = PROTECTED)
@Builder(access = PRIVATE)
@Getter
public class UserRoomsResponseDto {

    private String chatRoomId;
    private Integer receiverId;

    public static UserRoomsResponseDto of(String chatRoomId, Integer receiverId) {
        return UserRoomsResponseDto.builder()
                .chatRoomId(chatRoomId)
                .receiverId(receiverId)
                .build();
    }

    public static List<UserRoomsResponseDto> from(List<String> roomIds, List<Integer> receiverIds) {
        ArrayList<UserRoomsResponseDto> userRoomsResponseDtoList = new ArrayList<>();

        for (int i = 0; i < roomIds.size(); i++) {
            userRoomsResponseDtoList.add(UserRoomsResponseDto.of(roomIds.get(i), receiverIds.get(i)));
        }

        return userRoomsResponseDtoList;
    }
}
