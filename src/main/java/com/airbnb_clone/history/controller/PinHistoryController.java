package com.airbnb_clone.history.controller;

import com.airbnb_clone.history.service.PersonalHistoryService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName    : com.airbnb_clone.history.controller
 * fileName       : PinHistoryController
 * author         : ipeac
 * date           : 24. 10. 4.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 10. 4.        ipeac       최초 생성
 */
@RestController
@RequestMapping("/api/pin-history")
@RequiredArgsConstructor
@Hidden
public class PinHistoryController {
    private final PersonalHistoryService personalHistoryService;

    //스웨거에서 안보이게 처리
    @PostMapping("/v1")
    public void saveHistory(@RequestParam(value = "userNo") Long userNo,@RequestParam(value = "pinNo") Long pinId) {
        personalHistoryService.saveHistory(userNo, pinId);
    }
}
