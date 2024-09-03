package com.airbnb_clone.pin.controller;

import com.airbnb_clone.common.global.response.ApiResponse;
import com.airbnb_clone.pin.domain.dto.request.TemporaryPinCreateRequestDTO;
import com.airbnb_clone.pin.domain.dto.response.TemporaryPinDetailResponseDTO;
import com.airbnb_clone.pin.service.PinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/pin/temp/{temp-pin-no}/v1")
    public ApiResponse<TemporaryPinDetailResponseDTO> getTempPin(@PathVariable("temp-pin-no") String tempPinNo) {
        return ApiResponse.of("임시핀 조회 성공", HttpStatus.OK.value(), pinService.getTempPin(tempPinNo));
    }

    @PostMapping("/pin/temp/v1")
    public ApiResponse<String> uploadImage(@RequestBody TemporaryPinCreateRequestDTO temporaryPinCreateRequestDTO) {
        return ApiResponse.of("핀이 정상적으로 임시저장되었습니다.", HttpStatus.OK.value(), pinService.createTempPin(temporaryPinCreateRequestDTO).toString());
    }
}
