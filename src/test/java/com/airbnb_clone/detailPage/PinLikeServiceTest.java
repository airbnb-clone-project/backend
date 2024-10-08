package com.airbnb_clone.detailPage;


import com.airbnb_clone.detaile.repository.PinLikeRepository;
import com.airbnb_clone.detaile.service.PinLikeService;
import static org.junit.jupiter.api.Assertions.*;

import com.airbnb_clone.exception.detaile.DuplicatePinLikeException;
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
    void add_pin_like__success() {
        Long pinId = 1L;
        Long userId = 2L;
        int emojiNo = 1;

        given(pinLikeRepository.hasUserReactedToPin(pinId, userId)).willReturn(false);
        pinLikeService.addPinLike(pinId, userId, emojiNo);
        then(pinLikeRepository).should().addPinLike(pinId, userId, emojiNo);
        //given, then, willreturn, should
    }

    @Test
    @DisplayName("핀반응 중복확인")
    void has_user_Reacted_to_pin(){
        Long pinId = 1L;
        Long userId = 2L;
        int emojiNo = 1;

        given(pinLikeRepository.hasUserReactedToPin(pinId, userId)).willReturn(true);


        assertThrows(DuplicatePinLikeException.class, () -> {
            pinLikeService.addPinLike(pinId, userId, emojiNo);
        });
        //'addpinlike 호출확인
        then(pinLikeRepository).should(never()).addPinLike(anyLong(), anyLong(), anyInt());
    }
}
