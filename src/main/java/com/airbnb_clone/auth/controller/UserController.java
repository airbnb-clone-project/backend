package com.airbnb_clone.auth.controller;

import com.airbnb_clone.auth.dto.oauth2.MoreUserRegisterRequest;
import com.airbnb_clone.auth.dto.users.NewPasswordRequest;
import com.airbnb_clone.auth.dto.users.UserRegisterRequest;
import com.airbnb_clone.auth.dto.users.UsersProfileRequest;
import com.airbnb_clone.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartRequest;

import java.util.Map;


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
 * 24. 9. 13.        DK       profile 가져오는 controller 추가
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

    @PostMapping("/new-password") // password, newPassword
    public ResponseEntity<?> changePassword(@RequestBody NewPasswordRequest request) {
        return userService.changePassword(request);
    }

    // profile 불러오기
    @GetMapping("/profiles") // description, lastName, firstName
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        return userService.getProfile(request);
    }

    // profile 저장
    @PostMapping("/profiles") // description, lastName, firstName
    public ResponseEntity<?> setProfile(@RequestBody UsersProfileRequest usersProfileRequest, HttpServletRequest request) {
        return userService.setProfile(request, usersProfileRequest);
    }

    // cascade 관련 문제가 있기 때문에 수정이 필요, 호출하지 않는 한 에러 메세지가 나오진 않음
    @DeleteMapping("/erase-accounts")
    public ResponseEntity<?> eraseAccounts(HttpServletRequest request) {

        return userService.eraseAccounts(request);
    }

    @GetMapping("/accounts")
    public ResponseEntity<?> getAccount(HttpServletRequest request) {
        return userService.getAccount(request);
    }

    // 
    @ResponseBody
    @GetMapping("/profile-images")
    public Map<String, Object> profileImageUpload(MultipartRequest request) {

        return;
    }

}
