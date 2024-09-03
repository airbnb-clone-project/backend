package com.airbnb_clone.common.annotation;

import com.airbnb_clone.config.db.auditing.MongDBAuditingConfig;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * packageName    : com.airbnb_clone.common.annotation
 * fileName       : DataMongoTestAnnotation
 * author         : ipeac
 * date           : 24. 9. 3.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 9. 3.        ipeac       최초 생성
 */
@Retention(RUNTIME)
@Target(ElementType.TYPE)
@DataMongoTest
@Import(MongDBAuditingConfig.class)
@ActiveProfiles("test")
public @interface DataMongoTestAnnotation {
}
