package com.airbnb_clone.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * packageName    : com.st.eighteen_be.config.openapi
 * fileName       : OpenApiConfig
 * author         : ipeac
 * date           : 24. 5. 5.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 24. 5. 5.        ipeac       최초 생성
 */
@Profile(value = "!prod")
@Configuration
public class SwaggerConfig {

    public static final String[] PACKAGES = {"com.airbnb_clone"};
    public static final String[] PATHS = {"/api/**"};

    @Bean
    public OpenAPI customOpenAPI() {
        Server localServer = setCustomServer("http://localhost:8008", "Local Server");
        Server devServer = setCustomServer("http://34.46.135.133", "Dev Server");

        return new OpenAPI()
                       .info(
                               new Info()
                                       .title("AIRBNB CLONE API")
                                       .version("v1")
                                       .description("")
                       )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth")) // SecurityRequirement를 추가하여 인증 요구사항을 설정
                .components(new io.swagger.v3.oas.models.Components() // SecurityScheme을 추가하여 인증 스키마를 설정
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP) // HTTP 타입의 인증 스키마
                                        .scheme("bearer") // Bearer 토큰 사용
                                        .bearerFormat("JWT") // JWT 형식의 토큰
                                        .in(SecurityScheme.In.HEADER) // 헤더에 포함
                                        .name("Authorization") // 헤더 이름
                        )
                        .addSecuritySchemes("refreshToken",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY) // API Key 타입의 인증 스키마
                                        .in(SecurityScheme.In.HEADER) // 헤더에 포함
                                        .name("Refresh") // 헤더 이름
                        )
                )
                .servers(List.of(localServer, devServer));
    }

    private static Server setCustomServer(String url, String description) {
        Server localServer = new Server();

        localServer.setUrl(url);
        localServer.setDescription(description);

        return localServer;
    }

    @Bean
    public GroupedOpenApi apiV1() {
        return GroupedOpenApi.builder()
                       .packagesToScan(PACKAGES)
                       .pathsToMatch(PATHS)
                       .group("v1")
                       .build();
    }
}
