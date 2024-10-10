package com.airbnb_clone.auth.controller;

import com.airbnb_clone.auth.dto.oauth2.AdditionalUserRegisterRequest;
import com.airbnb_clone.auth.dto.users.NewPasswordRequest;
import com.airbnb_clone.auth.dto.users.UserRegisterRequest;
import com.airbnb_clone.auth.dto.users.UsersProfileRequest;
import com.airbnb_clone.auth.service.ProfileImageService;
import com.airbnb_clone.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
 * 24. 9. 13.        DK       profile 가져오는 controller 추가
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "유저 API", description = "유저 API")
public class UserController {

    private final UserService userService;
    private final ProfileImageService profileImageService;

    @Operation(summary = "이메일 회원가입", description = "이메일 회원가입")
    @PostMapping("/register") // username, password
    public ResponseEntity<?> registerUser(@RequestBody UserRegisterRequest request, HttpServletResponse response) {
        return userService.registerUser(request, response);
    }

    @Operation(summary = "회원 추가정보 입력", description = "회원 추가정보 입력")
    @PostMapping("/additional-information") // username, birthday, gender, spokenLanguage, country
    public ResponseEntity<?> addAdditionalUserInformation(@RequestBody AdditionalUserRegisterRequest request) {
        return userService.saveAdditionalUserInformation(request);
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호 변경")
    @PostMapping("/new-password") // password, newPassword
    public ResponseEntity<?> changePassword(@RequestBody NewPasswordRequest request) {
        return userService.changePassword(request);
    }

    // profile 불러오기
    @Operation(summary = "profile 불러오기", description = "profile 불러오기")
    @GetMapping("/profile") // description, lastName, firstName
    public ResponseEntity<?> getProfile() {
        return userService.getProfile();
    }

    // profile 저장
    @Operation(summary = "profile 정보 저장", description = "profile 정보 저장")
    @PostMapping("/profile") // description, lastName, firstName
    public ResponseEntity<?> setProfile(@RequestBody UsersProfileRequest usersProfileRequest) {
        return userService.setProfile(usersProfileRequest);
    }

    // cascade 관련 문제가 있기 때문에 수정이 필요, 호출하지 않는 한 에러 메세지가 나오진 않음
//    @DeleteMapping("/erase-accounts")
//    public ResponseEntity<?> eraseAccounts(HttpServletRequest request) {
//
//        return userService.eraseAccounts(request);
//    }

    @Operation(summary = "account 불러오기", description = "account 불러오기")
    @GetMapping("/account")
    public ResponseEntity<?> getAccount() {
        return userService.getAccount();
    }

    // 이미지 저장
    @Operation(summary = "프로필 이미지 저장", description = "프로필 이미지 저장")
    @ResponseBody
    @PostMapping("/profile-image")
    public ResponseEntity<?> profileImageUpload(@RequestParam String profileImageUrl) {
        return profileImageService.saveProfileImageMetadata(profileImageUrl);
    }

    @Operation(summary = "프로필 이미지 불러오기", description = "프로필 이미지 불러오기")
    @GetMapping("/profile-image")
    public ResponseEntity<?> getProfileImage() {
        return profileImageService.getProfileImage();
    }

    @GetMapping("/pre-signed-url")
    public ResponseEntity<?> getPresignedUrl(@RequestParam String contextType) {
        return profileImageService.generatePresignedUrl(contextType);
    }
}
