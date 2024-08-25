package com.airbnb_clone.common.annotation;

import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Profile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.springframework.boot.jdbc.EmbeddedDatabaseConnection.H2;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.ANY;

/**
 * packageName    : com.airbnb_clone.common.annotation
 * fileName       : DataJdbcTestAnnotation
 * author         : ipeac
 * date           : 24. 8. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 25.        ipeac       최초 생성
 */
@Retention(RUNTIME)
@Target(ElementType.TYPE)
@DataJdbcTest
@AutoConfigureTestDatabase(replace = ANY, connection = H2)
@Profile("test")
public @interface DataJdbcTestAnnotation {
}
