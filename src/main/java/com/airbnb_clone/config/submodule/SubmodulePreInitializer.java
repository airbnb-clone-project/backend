package com.airbnb_clone.config.submodule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * packageName    : com.airbnb_clone.config.submodule
 * fileName       : SubmodulePreInitializer
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
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SubmodulePreInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        this.updateSubmodule();
    }

    private void updateSubmodule() {
        grantPermission();

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "setup/submodules/submodule-setup.sh");

        try {
            Process startedProcess = processBuilder.start();
            int exitCode = startedProcess.waitFor();

            if (isSuccess(exitCode)) {
                log.info("서브모듈을 성공적으로 업데이트했습니다");
                return;
            }

            if (isFailed(exitCode)) {
                log.error("서브모듈 업데이트에 실패했습니다 (하위 에러 확인)");

                try (BufferedReader br = new BufferedReader(new InputStreamReader(startedProcess.getErrorStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        log.error(line);
                    }
                }
            }
        } catch (InterruptedException | IOException e) {
            log.error("서브모듈 업데이트 중 오류가 발생했습니다", e);
        }
    }

    private void grantPermission() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", "chmod +x setup/submodules/submodule-setup.sh");

        try {
            Process startedProcess = processBuilder.start();
            int exitCode = startedProcess.waitFor();

            if (isSuccess(exitCode)) {
                log.info("권한을 성공적으로 부여했습니다");
                return;
            }

            if (isFailed(exitCode)) {
                log.error("권한 부여에 실패했습니다 (하위 에러 확인)");

                try (BufferedReader br = new BufferedReader(new InputStreamReader(startedProcess.getErrorStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        log.error(line);
                    }
                }
            }
        } catch (InterruptedException | IOException e) {
            log.error("권한 부여 중 오류가 발생했습니다", e);
        }
    }

    private static boolean isFailed(int exitCode) {
        return exitCode != 0;
    }

    private static boolean isSuccess(int exitCode) {
        return exitCode == 0;
    }
}
