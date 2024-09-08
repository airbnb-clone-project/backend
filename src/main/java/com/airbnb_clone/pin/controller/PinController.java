package com.airbnb_clone.pin.controller;

import com.airbnb_clone.common.global.response.ApiResponse;
import com.airbnb_clone.pin.domain.pin.dto.request.PinCreateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.request.TemporaryPinCreateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.request.TemporaryPinUpdateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.response.TemporaryPinDetailResponseDTO;
import com.airbnb_clone.pin.domain.pin.dto.response.TemporaryPinsResponseDTO;
import com.airbnb_clone.pin.service.PinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public HttpEntity<ApiResponse<TemporaryPinDetailResponseDTO>> getTempPin(@PathVariable("temp-pin-no") String tempPinNo) {
        return ResponseEntity.ok(
                ApiResponse.of("임시핀 조회 성공", HttpStatus.OK.value(), pinService.getTempPin(tempPinNo))
        );
    }

    @PutMapping("/pin/temp/{temp-pin-no}/v1")
    public HttpEntity<ApiResponse<String>> updateTempPin(@PathVariable("temp-pin-no") String tempPinNo, @RequestBody TemporaryPinUpdateRequestDTO temporaryPinCreateRequestDTO) {
        pinService.updateTempPin(tempPinNo, temporaryPinCreateRequestDTO);

        return ResponseEntity.ok(
                ApiResponse.of("임시핀이 정상적으로 수정되었습니다.", HttpStatus.OK.value(), null)
        );
    }

    @GetMapping("/pin/temps/{user-no}/v1")
    public HttpEntity<ApiResponse<List<TemporaryPinsResponseDTO>>> getTempPins(@PathVariable("user-no") Long userNo) {
        return ResponseEntity.ok(
                ApiResponse.of("임시핀 목록 조회 성공", HttpStatus.OK.value(), pinService.getTempPins(userNo))
        );
    }

    @PostMapping("/pin/temp/v1")
    public HttpEntity<ApiResponse<String>> uploadImage(@RequestBody @Valid TemporaryPinCreateRequestDTO temporaryPinCreateRequestDTO) {
        return ResponseEntity.ok(
                ApiResponse.of("핀이 정상적으로 임시저장되었습니다.", HttpStatus.OK.value(), pinService.createTempPin(temporaryPinCreateRequestDTO).toString())
        );
    }

    //TODO : 향후 헤더의 토큰을 가져와서 유저번호를 추출하여 저장하는 로직으로 변경해야한다.
    @PostMapping("/pin/v1")
    public HttpEntity<ApiResponse<Long>> savePin(@RequestBody @Valid PinCreateRequestDTO pinCreateRequestDTO) {
        return ResponseEntity.ok(
                ApiResponse.of("핀이 정상적으로 저장되었습니다.", HttpStatus.OK.value(), pinService.savePin(pinCreateRequestDTO))
        );
    }
}
