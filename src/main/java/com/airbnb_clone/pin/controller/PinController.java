package com.airbnb_clone.pin.controller;

import com.airbnb_clone.auth.dto.users.CustomUserDetails;
import com.airbnb_clone.common.global.response.ApiResponse;
import com.airbnb_clone.pin.domain.pin.dto.request.PinCreateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.request.PinUpdateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.request.TemporaryPinCreateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.request.TemporaryPinUpdateRequestDTO;
import com.airbnb_clone.pin.domain.pin.dto.response.PinMainResponseDTO;
import com.airbnb_clone.pin.domain.pin.dto.response.TemporaryPinDetailResponseDTO;
import com.airbnb_clone.pin.domain.pin.dto.response.TemporaryPinsResponseDTO;
import com.airbnb_clone.pin.service.PinAuthHelper;
import com.airbnb_clone.pin.service.PinService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

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
@Tag(name = "핀 API", description = "핀 API")
public class PinController {
    private final PinService pinService;
    private final PinAuthHelper pinAuthHelper;

    @Operation(summary = "임시핀 조회", description = "임시핀 조회")
    @GetMapping("/pin/temp/{temp-pin-no}/v1")
    public HttpEntity<ApiResponse<TemporaryPinDetailResponseDTO>> getTempPin(
            @Parameter(description = "임시핀 번호", example = "66faa20f0893f5746fdb91d0", required = true)
            @PathVariable("temp-pin-no") String tempPinNo
    ) {
        return ResponseEntity.ok(
                ApiResponse.of("임시핀 조회 성공", HttpStatus.OK.value(), pinService.getTempPin(tempPinNo))
        );
    }

    @Operation(summary = "임시핀 수정", description = "임시핀 수정")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/pin/temp/{temp-pin-no}/v1")
    @PreAuthorize("isAuthenticated()")
    public HttpEntity<ApiResponse<String>> updateTempPin(
            @Parameter(description = "임시핀 번호", example = "66faa20f0893f5746fdb91d0", required = true)
            @PathVariable("temp-pin-no") String tempPinNo,

            @RequestBody TemporaryPinUpdateRequestDTO temporaryPinCreateRequestDTO,

            @AuthenticationPrincipal(errorOnInvalidType = true)  CustomUserDetails userDetails
    ) {
        pinAuthHelper.isPinOwnerForTempPin(tempPinNo, userDetails.getUsername());

        pinService.updateTempPin(tempPinNo, temporaryPinCreateRequestDTO);

        return ResponseEntity.ok(
                ApiResponse.of("임시핀이 정상적으로 수정되었습니다.", HttpStatus.OK.value(), null)
        );
    }

    @Operation(summary = "해당 사용자의 임시핀 전체 조회", description = "해당 사용자의 임시핀 전체 조회")
    @GetMapping("/pin/temps/{user-email}/v1")
    public HttpEntity<ApiResponse<List<TemporaryPinsResponseDTO>>> getTempPins(@PathVariable("user-email") String userEmail) {
        
        return ResponseEntity.ok(
                ApiResponse.of("임시핀 목록 조회 성공", HttpStatus.OK.value(), pinService.getTempPins(userEmail))
        );
    }

    /**
     * 임시핀 생성 요청
     *
     * @param temporaryPinCreateRequestDTO 임시핀 생성 요청 DTO
     * @return 생성된 임시핀의 ObjectId
     */
    @Operation(summary = "임시핀 생성", description = "임시핀 생성",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(mediaType = MULTIPART_FORM_DATA_VALUE,
                    schema = @Schema(implementation = TemporaryPinCreateRequestDTO.class))),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            })
    @PostMapping("/pin/temp/v1")
    @PreAuthorize("isAuthenticated()")
    public Mono<HttpEntity<ApiResponse<String>>> uploadImage(
            @ModelAttribute TemporaryPinCreateRequestDTO temporaryPinCreateRequestDTO, @AuthenticationPrincipal(errorOnInvalidType = true)  CustomUserDetails userDetails
    ) {
        return pinService.createTempPin(temporaryPinCreateRequestDTO,userDetails.getUsername())
                .map(objectId -> ResponseEntity.ok(
                        ApiResponse.of("핀이 정상적으로 임시저장되었습니다.", HttpStatus.OK.value(), objectId.toString())
                ));
    }

    @Operation(summary = "핀 저장", description = "핀 저장")
    @PostMapping("/pin/v1")
    public HttpEntity<ApiResponse<Long>> savePin(@RequestBody @Valid PinCreateRequestDTO pinCreateRequestDTO) {
        return ResponseEntity.ok(
                ApiResponse.of("핀이 정상적으로 저장되었습니다.", HttpStatus.OK.value(), pinService.savePin(pinCreateRequestDTO))
        );
    }

    @Operation(summary = "핀 수정", description = "핀 수정")
    @PutMapping("/pin/{pin-no}/v1")
    public HttpEntity<ApiResponse<Long>> updatePin(@PathVariable("pin-no") Long pinNo, @RequestBody @Valid PinUpdateRequestDTO pinUpdateRequestDTO, HttpServletRequest request) {
        pinAuthHelper.isPinOwnerForPin(pinNo, request);

        return ResponseEntity.ok(
                ApiResponse.of("핀이 정상적으로 수정되었습니다.", HttpStatus.OK.value(), pinService.updatePin(pinNo, pinUpdateRequestDTO))
        );
    }

    @Operation(summary = "핀 삭제", description = "핀 삭제")
    @DeleteMapping("/pin/{pin-no}/v1")
    public HttpEntity<ApiResponse<?>> deletePin(@PathVariable("pin-no") Long pinNo, HttpServletRequest request) {
        pinAuthHelper.isPinOwnerForPin(pinNo, request);

        pinService.deletePinSoftly(pinNo);

        return ResponseEntity.ok(
                ApiResponse.of("핀이 정상적으로 삭제되었습니다.", HttpStatus.OK.value(), Optional.empty())
        );
    }

    @Hidden
    @Operation(summary = "모든 핀 레디스 업데이트 ( 편의 api )", description = "모든 핀 레디스 업데이트")
    @PostMapping("/pin/force-update-to-redis-batch/v1")
    public HttpEntity<ApiResponse<?>> forceUpdateToRedisBatch() {
        pinService.cacheAllPinsToRedis();

        return ResponseEntity.ok(
                ApiResponse.of("모든 핀이 정상적으로 강제로 레디스에 업데이트되었습니다.. (부하 주의)", HttpStatus.OK.value(), Optional.empty())
        );
    }

    @Operation(summary = "메인 화면에 노출할 핀 조회", description = "메인 화면에 노출할 핀 조회")
    @GetMapping("/pin/v1")
    public HttpEntity<ApiResponse<List<PinMainResponseDTO>>> getMainPins(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int pageSize, @AuthenticationPrincipal  CustomUserDetails userDetails) {
        Long userNo = Optional.ofNullable(userDetails)
                .map(CustomUserDetails::getUserNo)
                .orElse(null);

        return ResponseEntity.ok(
                ApiResponse.of("메인 화면에 노출할 핀 조회 성공", HttpStatus.OK.value(), pinService.getMainPins(page, pageSize, userNo))
        );
    }
}
