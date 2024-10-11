package com.airbnb_clone.detailPage;


import com.airbnb_clone.detaile.repository.PinLikeRepository;
import com.airbnb_clone.detaile.service.PinLikeService;

import static org.assertj.core.api.Assertions.*;

import com.airbnb_clone.exception.detaile.PinLikeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class PinLikeServiceTest {

    @Mock
    private PinLikeRepository pinLikeRepository;

    //injectmocks
    @InjectMocks
    private PinLikeService pinLikeService;

    @BeforeEach
    void beforeEach() {
        pinLikeRepository = Mockito.mock(PinLikeRepository.class);
        //Mockito.mock
        pinLikeService = new PinLikeService(pinLikeRepository);
    }

    @Test
    @DisplayName("핀반응추가")
    void add_pin_like_success() {
        Long pinId = 1L;
        Long userId = 2L;
        int emojiNo = 1;

        given(pinLikeRepository.getUserEmojiForPin(pinId, userId)).willReturn(null);
        pinLikeService.addPinLike(pinId, userId, emojiNo);
        then(pinLikeRepository).should().addPinLike(pinId, userId, emojiNo);
    }

    @Test
    @DisplayName("핀반응 업데이트_다른 이모지를 추가한 경우")
    void update_pin_like_success() {
        Long pinId = 1L;
        Long userId = 2L;
        int emojiNo = 2;

        given(pinLikeRepository.getUserEmojiForPin(pinId, userId)).willReturn(1);
        pinLikeService.addPinLike(pinId, userId, emojiNo);
        then(pinLikeRepository).should().updatePinLike(pinId, userId, emojiNo);
        then(pinLikeRepository).should(never()).addPinLike(anyLong(), anyLong(), anyInt());
    }

    @Test
    @DisplayName("핀반응 업데이트_같은 이모지를 추가한 경우")
    void same_pin_like_success() {
        Long pinId = 1L;
        Long userId = 2L;
        int emojiNo = 2;

        given(pinLikeRepository.getUserEmojiForPin(pinId, userId)).willReturn(2);
        pinLikeService.addPinLike(pinId, userId, emojiNo);
        then(pinLikeRepository).should(never()).addPinLike(anyLong(), anyLong(), anyInt());
        then(pinLikeRepository).should(never()).updatePinLike(anyLong(), anyLong(), anyInt());
    }

    @Test
    @DisplayName("반응을 추가한 경우 삭제 성공 테스트")
    void delete_pin_like__success() {
        Long pinId = 1L;
        Long userId = 2L;
        Integer existingEmojiNo = 1;

        given((pinLikeRepository.getUserEmojiForPin(pinId, userId))).willReturn(existingEmojiNo);
        pinLikeService.deletePinLike(pinId, userId);
        then (pinLikeRepository).should().deletePinLike(pinId, userId);
    }

    @Test
    @DisplayName("반응을 추가하지 않은 경우 예외 발생 테스트")
    void delete_pin_like__fail() {
        Long pinId = 1L;
        Long userId = 2L;
        given(pinLikeRepository.getUserEmojiForPin(pinId,userId)).willReturn(null);

        assertThatThrownBy(() -> pinLikeService.deletePinLike(pinId, userId))
                .isInstanceOf(PinLikeNotFoundException.class)
                .hasMessage("해당 핀에 추가한 반응이 없습니다");
        then(pinLikeRepository).should(never()).deletePinLike(anyLong(), anyLong());
    }
}
