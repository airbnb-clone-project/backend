package com.airbnb_clone.image.validator;

import com.airbnb_clone.exception.ErrorCode;
import com.airbnb_clone.exception.image.ContentTypeNotMatchException;
import org.springframework.http.MediaType;

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
    private static final Set<MediaType> IMAGE_MIME_TYPES = Set.of(
            MediaType.IMAGE_JPEG,
            MediaType.IMAGE_PNG,
            MediaType.IMAGE_GIF,
            MediaType.valueOf("image/bmp"),
            MediaType.valueOf("image/webp"),
            MediaType.valueOf("image/tiff"),
            MediaType.valueOf("image/svg+xml")
    );

    public static boolean isValidImageContentType(String contentType) {
        MediaType mediaType = MediaType.parseMediaType(contentType);
        if (IMAGE_MIME_TYPES.contains(mediaType)) {
            return true;
        }

        throw new ContentTypeNotMatchException(ErrorCode.CONTENT_TYPE_NOT_MATCH);
    }
}
