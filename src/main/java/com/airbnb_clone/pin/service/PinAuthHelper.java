package com.airbnb_clone.pin.service;

import com.airbnb_clone.auth.jwt.JwtUtil;
import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.pin.PinAuthException;
import com.airbnb_clone.exception.pin.PinNotFoundException;
import com.airbnb_clone.pin.domain.pin.PinTemp;
import com.airbnb_clone.pin.repository.PinMongoRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

/**
 * packageName    : com.airbnb_clone.pin.service
 * fileName       : PinAuthHelper
 * author         : ipeac
 * date           : 24. 9. 29.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 29.        ipeac       최초 생성
 */
@Component
@RequiredArgsConstructor
public class PinAuthHelper {
    private final PinMongoRepository pinMongoRepository;
    private final JwtUtil jwtUtil;

    public void isPinOwnerForTempPin(String tempPinNo, HttpServletRequest servletRequest) {
        Long usersNo = getUsersNoFromJwt(servletRequest);

        PinTemp foundPinTemp = pinMongoRepository.findPinTempByUserNo(usersNo)
                .orElseThrow(() -> new PinNotFoundException(ErrorCode.PIN_NOT_FOUND));

        boolean isOwner = foundPinTemp.isOwner(usersNo, new ObjectId(tempPinNo));
        if(!isOwner) {
            throw new PinAuthException(ErrorCode.PIN_AUTH_ERROR);
        }
    }

    private Long getUsersNoFromJwt(HttpServletRequest servletRequest) {
        String authorization = servletRequest.getHeader("Authorization");
        return jwtUtil.getUserNoFromAccessToken(authorization);
    }
}
