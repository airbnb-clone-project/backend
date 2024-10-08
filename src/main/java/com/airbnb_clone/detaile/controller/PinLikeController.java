package com.airbnb_clone.detaile.controller;

import com.airbnb_clone.detaile.service.PinLikeService;
import com.airbnb_clone.exception.detaile.PinLikeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class PinLikeController {
    private final PinLikeService pinLikeService;

    @PostMapping("api/pin/like/{pinId}/v1")
    public ResponseEntity<String> addPinLike(@PathVariable Long pinId, @RequestParam Long userNo, @RequestParam int emojiNo) {
        try {
                pinLikeService.addPinLike(pinId, userNo, emojiNo);
                return ResponseEntity.ok("핀 반응이 추가 또는 변경되었습니다.");
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다");
        }
    }

    @DeleteMapping("api/pin/like/{pinId}/v1")
    public ResponseEntity<String> deletePinLike(@PathVariable Long pinId, @RequestParam Long userNo) {
        try {
            pinLikeService.deletePinLike(pinId, userNo);
            return ResponseEntity.ok("핀 반응이 삭제되었습니다");
        }catch (PinLikeNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다");
        }
    }

}
