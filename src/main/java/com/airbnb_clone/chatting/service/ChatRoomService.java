package com.airbnb_clone.chatting.service;

import com.airbnb_clone.chatting.domain.ChatRoom;
import com.airbnb_clone.chatting.repository.ChatRoomRepository;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewReqDto;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.ChatRoomNewResDto;
import com.airbnb_clone.chatting.repository.Dto.chatRoom.UserRoomsResponseDto;
import com.airbnb_clone.exception.chatting.ChatRoomNotFoundException;
import com.airbnb_clone.exception.chatting.DuplicateChatRoomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
//@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

//    @Transactional
    public ChatRoomNewResDto save(ChatRoomNewReqDto chatRoomNewRequestDto) {
        duplicateChatRoomVerification(chatRoomNewRequestDto.getParticipants());

        ChatRoom chatRoom = ChatRoom.of(chatRoomNewRequestDto.getParticipants());
        String savedId = chatRoomRepository.save(chatRoom);

        return new ChatRoomNewResDto(savedId);
    }

    public ChatRoom findById(String id) {
        return chatRoomRepository.findById(id)
                .orElseThrow(ChatRoomNotFoundException::new);
    }

    public List<UserRoomsResponseDto> findUserRooms(Integer id) {
        List<ChatRoom> rooms = chatRoomRepository.findUserRooms(id);

        List<String> roomIds = rooms.stream()
                .map(ChatRoom::getNo)
                .map(ObjectId::toString)
                .toList();

        List<Integer> receiverIds = rooms.stream()
                .map(ChatRoom::getParticipants)
                .flatMap(Collection::stream)
                .filter(userId -> !userId.equals(id))
                .toList();

        return UserRoomsResponseDto.from(roomIds, receiverIds);
    }

    /**
     *
     * 채팅방이 존재하면 예외가 발생합니다.
     *
     * @param participants
     */
    private void duplicateChatRoomVerification(List<Integer> participants) {
        chatRoomRepository.checkExistingChatRoom(participants.get(0), participants.get(1)).ifPresent((chatRoom) -> {
            throw new DuplicateChatRoomException();
        });
    }
}
