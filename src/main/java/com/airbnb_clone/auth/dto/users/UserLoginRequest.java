package com.airbnb_clone.auth.dto.users;

import lombok.Getter;
import lombok.Setter;

/**
 * packageName    : com.airbnb_clone.dto.user;
 * fileName       : UserLoginRequest
 * author         : DK
 * date           : 24. 8. 18.
 * description    : 유저 로그인에 필요한 값에 대한 Requset DTO
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 18.        DK       최초 생성, 코드 추가
 */
@Setter @Getter
public class UserLoginRequest {

    private String username;
    private String password;

}
