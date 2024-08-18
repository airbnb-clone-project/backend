package com.airbnb_clone.chatting.repository.Dto.chatRoom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class ChatRoomNewReqDto {

    private List<Integer> participants;
}
