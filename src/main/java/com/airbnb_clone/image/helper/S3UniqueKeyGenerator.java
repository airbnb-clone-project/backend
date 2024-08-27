package com.airbnb_clone.image.helper;

import java.util.UUID;

/**
 * packageName    : com.airbnb_clone.image.helper
 * fileName       : S3UniqueKeyGenerator
 * author         : ipeac
 * date           : 24. 8. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 27.        ipeac       최초 생성
 */
public class S3UniqueKeyGenerator {
    public static String generateUniqueKey() {
        return UUID.randomUUID().toString();
    }
}
