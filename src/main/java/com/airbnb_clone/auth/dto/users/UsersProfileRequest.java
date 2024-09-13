package com.airbnb_clone.auth.dto.users;

import lombok.Getter;
import lombok.Setter;

/**
 * packageName    : com.airbnb_clone.auth.dto.users
 * fileName       : UsersProfileRequest
 * author         : doungukkim
 * date           : 2024. 9. 13.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024. 9. 13.        doungukkim       최초 생성
 */
@Getter
@Setter
public class UsersProfileRequest {
    String firstName;
    String lastName;
    String description;
    String username;

}
