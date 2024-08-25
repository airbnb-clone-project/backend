package com.airbnb_clone.config;

import com.airbnb_clone.config.dotenv.SubmoduleEnvironmentLoaderConfig;
import com.airbnb_clone.config.submodule.SubmodulePreInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * packageName    : com.airbnb_clone.config
 * fileName       : CustomContextInitializer
 * author         : ipeac
 * date           : 24. 8. 25.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 8. 25.        ipeac       최초 생성
 */
public class CustomContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        new SubmodulePreInitializer().initialize(applicationContext);
        new SubmoduleEnvironmentLoaderConfig().initialize(applicationContext);
    }
}
