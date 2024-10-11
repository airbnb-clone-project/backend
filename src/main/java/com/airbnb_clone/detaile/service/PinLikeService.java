package com.airbnb_clone.detaile.service;


import com.airbnb_clone.detaile.repository.PinLikeRepository;
import com.airbnb_clone.exception.detaile.PinLikeNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PinLikeService {
    private final PinLikeRepository pinLikeRepository;

    //핀 추가
    @Transactional
    public void addPinLike(Long pinNo, Long userNo, int emojiNo ) {
        //사용자가 해당 핀에 반응했는지 확인
        Integer returnEmogiNo = pinLikeRepository.getUserEmojiForPin(pinNo, userNo);

        if(returnEmogiNo == null) {//반응이 없는 경우 : 새로운 반응 추가
            pinLikeRepository.addPinLike(pinNo, userNo, emojiNo);
            log.info("새로운 이모지({}) 추가", emojiNo);
        } else if (returnEmogiNo == emojiNo) {//같은 이모지로 반응한 경우
            log.info("같은 이모지({})가 이미 있음", emojiNo);
        }else {//다른 이모지로 반응한 경우
            pinLikeRepository.updatePinLike(pinNo, userNo, emojiNo);
            log.info("다른 이모지({})로 업데이트",emojiNo);
        }
    }

    //핀 삭제
    @Transactional
    public void deletePinLike(Long pinNo, Long userNo) {
        //반응 추가했는지 확인
        Integer emogiNo = pinLikeRepository.getUserEmojiForPin(pinNo, userNo);
        if (emogiNo == null) {//반응이 없는 경우
            throw new PinLikeNotFoundException("해당 핀에 추가한 반응이 없습니다");
        }else {//반응 삭제
            pinLikeRepository.deletePinLike(pinNo, userNo);
        }
    }

}
