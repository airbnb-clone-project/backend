package com.airbnb_clone.auth.controller;

import com.airbnb_clone.auth.service.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * packageName    : com.airbnb_clone.auth.controller;
 * fileName       : ReissueController
 * author         : DK
 * date           : 24. 8. 23.
 * description    : reissue controller
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 23.        DK       최초 생성
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class ReissueController {

    private final ReissueService reissueService;

    /*
        refresh 토큰을 이용해 access 토큰 재발급
        HttpServeltRequest를 통해 토큰이 담긴 쿠키를 전달받는다.
        access 토큰을 요청 하기 위해 refresh token 만 보면 되기 때문에 바디에 뭐가 들었건 딱히 상관 없음
     */
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueUserToken(HttpServletResponse response, HttpServletRequest request) {
        return reissueService.reissueRefreshToken(request, response);
    }
}
