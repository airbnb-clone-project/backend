package com.airbnb_clone.auth.dto.users;

import lombok.Getter;
import lombok.Setter;

/**
 * packageName    : com.airbnb_clone.auth.dto.users
 * fileName       : NewPasswordRequest
 * author         : doungukkim
 * date           : 2024. 8. 30.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024. 8. 30.        doungukkim       최초 생성
 */
@Setter
@Getter
public class NewPasswordRequest {
    private String password;
    private String newPassword;
}
