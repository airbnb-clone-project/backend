package com.airbnb_clone.config.dotenv;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName    : com.airbnb_clone.config.dotenv
 * fileName       : SubmoduleEnvironmentLoaderConfig
 * author         : ipeac
 * date           : 24. 8. 24.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 24.        ipeac       최초 생성
 */
@Configuration
@Slf4j
@Order(SubmoduleEnvironmentLoaderConfig.ON_NEXT_SUBMODULE_PRE_INITIALIZATION)
public class SubmoduleEnvironmentLoaderConfig implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public static final int ON_NEXT_SUBMODULE_PRE_INITIALIZATION = Ordered.HIGHEST_PRECEDENCE + 1;
    public static final String S3_SECRET_FILENAME = "backend-config-secret/s3-secret-file.env";

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        log.info("SubmoduleEnvironmentLoaderConfig loaded");

        Dotenv dotenv = Dotenv.configure().filename(S3_SECRET_FILENAME).load();

        Map<String, Object> dotenvProperties = new HashMap<>();
        dotenvProperties.put("AWS_ACCESS_KEY", dotenv.get("AWS_ACCESS_KEY"));
        dotenvProperties.put("AWS_SECRET_KEY", dotenv.get("AWS_SECRET_KEY"));

        applicationContext.getEnvironment().getPropertySources().addFirst(new MapPropertySource("dotenvProperties", dotenvProperties));
    }
}
