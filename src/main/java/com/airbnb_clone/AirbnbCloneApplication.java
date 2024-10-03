package com.airbnb_clone;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		servers = {
				@Server(url = "http://localhost:8008", description = "local server"),
				@Server(url = "http://34.171.110.81:8008", description = "swagger server")
		}
)
public class AirbnbCloneApplication {
	public static void main(String[] args) {
		SpringApplication.run(AirbnbCloneApplication.class, args);
	}
}
