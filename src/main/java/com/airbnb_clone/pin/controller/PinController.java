package com.airbnb_clone.pin.controller;

import com.airbnb_clone.common.global.response.ApiResponse;
import com.airbnb_clone.pin.repository.TemporaryPinCreateRequestDTO;
import com.airbnb_clone.pin.service.PinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName    : com.airbnb_clone.pin.controller
 * fileName       : PinController
 * author         : ipeac
 * date           : 24. 8. 21.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 21.        ipeac       최초 생성
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pins")
@Slf4j
public class PinController {
    private final PinService pinService;

    @PostMapping("/pin/upload-complete/v1")
    public ApiResponse<Long> uploadImage(@RequestBody TemporaryPinCreateRequestDTO temporaryPinCreateRequestDTO) {
        pinService.createTempPin(temporaryPinCreateRequestDTO);
        return ApiResponse.of("핀이 정상적으로 임시저장되었습니다.", HttpStatus.OK.value(), 1L);
    }
}
