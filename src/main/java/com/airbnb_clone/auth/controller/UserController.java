package com.airbnb_clone.auth.controller;

import com.airbnb_clone.auth.dto.oauth2.MoreUserRegisterRequest;
import com.airbnb_clone.auth.dto.users.NewPasswordRequest;
import com.airbnb_clone.auth.dto.users.UserRegisterRequest;
import com.airbnb_clone.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


/**
 * packageName    : package com.airbnb_clone.auth.controller;
 * fileName       : UserController
 * author         : DK
 * date           : 24. 8. 22.
 * description    : 유저 개인 정보
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 22.        DK       최초 생성
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/register") // username, password
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest request, HttpServletResponse response) {
        return userService.register(request, response);
    }

    @PostMapping("/more") // username, birthday, gender, spokenLanguage, country
    public ResponseEntity<?> more(@RequestBody MoreUserRegisterRequest request) {
        return userService.saveMoreUserInformation(request);
    }

    @PostMapping("/new-password")
    public ResponseEntity<?> changePassword(@RequestBody NewPasswordRequest request) {
        return userService.changePassword(request);
    }

    @GetMapping("/profiles") // description, lastName, firstName
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        return userService.getProfile(request);
    }

    // cascade 관련 문제가 있기 때문에 수정이 필요, 호출하지 않는 한 에러 메세지가 나오진 않음
    @DeleteMapping("/erase-accounts")
    public ResponseEntity<?> eraseAccounts(HttpServletRequest request) {
        return userService.eraseAccounts(request);
    }
}
