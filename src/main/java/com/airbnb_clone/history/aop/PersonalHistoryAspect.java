package com.airbnb_clone.history.aop;

import com.airbnb_clone.auth.jwt.JwtUtil;
import com.airbnb_clone.history.service.PersonalHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * packageName    : com.airbnb_clone.history.service
 * fileName       : PersonalHistoryService
 * author         : ipeac
 * date           : 24. 9. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 28.        ipeac       최초 생성
 */
@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class PersonalHistoryAspect {
    private final JwtUtil jwtUtil;
    private final PersonalHistoryService personalHistoryService;

    //TODO FIX -> 핀 상세 조회시 방문 이력 저장 알맞는 엔드포인트 찾아서 수정
    @AfterReturning(pointcut = "execution(* com.airbnb_clone.detile.controller.DetailPageController.getDetailPage(..) && args(pinId)", argNames = "pinId")
    public void saveHistoryAfterGetDetilPin(Long pinId, HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        //TODO FIX -> 토큰 파싱 방법 수정 (DK님)
        String modifiedToken = authorization.substring(7);
        Long userNo = jwtUtil.getUserNo(modifiedToken);

        personalHistoryService.saveHistory(userNo, pinId);
    }
}
