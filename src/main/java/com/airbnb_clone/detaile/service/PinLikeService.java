package com.airbnb_clone.detaile.service;


import com.airbnb_clone.detaile.repository.PinLikeRepository;
import com.airbnb_clone.exception.detaile.DuplicatePinLikeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PinLikeService {
    private final PinLikeRepository pinLikeRepository;

    //핀 추가
    @Transactional
    public void addPinLike(Long pinNo, Long userNo, int emojiNo ) {
        //중복 반응 방지
        boolean alreadyReacted = pinLikeRepository.hasUserReactedToPin(pinNo, userNo);
        if(alreadyReacted){
            throw new DuplicatePinLikeException("이미 해당 핀에 반응을 추가하셨습니다");
        }
        pinLikeRepository.addPinlike(pinNo, userNo, emojiNo);
    }

}
