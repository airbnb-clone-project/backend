package com.airbnb_clone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class AirbnbCloneApplication {
	public static void main(String[] args) {
		SpringApplication.run(AirbnbCloneApplication.class, args);
	}
}
