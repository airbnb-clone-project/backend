package com.airbnb_clone.image.validator;

import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.image.ContentTypeNotMatchException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * packageName    : com.airbnb_clone.image.validator
 * fileName       : ContentTypeValidator
 * author         : ipeac
 * date           : 24. 8. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 25.        ipeac       최초 생성
 */
public class ContentTypeValidator {
    private static final Set<String> IMAGE_MIME_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/bmp",
            "image/webp",
            "image/tiff",
            "image/svg+xml"
    ));

    public static boolean isValidImageContentType(String contentType) {
        if (IMAGE_MIME_TYPES.contains(contentType)) {
            return true;
        }

        throw new ContentTypeNotMatchException(ErrorCode.CONTENT_TYPE_NOT_MATCH);
    }
}
