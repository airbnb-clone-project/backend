package com.airbnb_clone.auth.dto.users;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


/**
 * packageName    : com.airbnb_clone.dto.user;
 * fileName       : UserRegisterRequest
 * author         : DK
 * date           : 24. 8. 16.
 * description    : 회원가입 request DTO
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 16.        DK       최초 생성
 * 24. 8. 17.        DK       코드 추가
 */
@Setter @Getter
public class UserRegisterRequest {
    private Long no;
    private String username;
    private String password;
    private LocalDate birthday;
}
