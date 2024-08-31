package com.airbnb_clone.utills;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class LocalDateTimeUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static LocalDateTime parse() {
        return LocalDateTime.parse(LocalDateTime.now().format(FORMATTER), FORMATTER);
    }

    public static String format(LocalDateTime localDateTime) {
        return localDateTime.format(FORMATTER);
    }
}
