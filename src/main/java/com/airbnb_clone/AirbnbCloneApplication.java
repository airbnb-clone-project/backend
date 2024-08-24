package com.airbnb_clone;

import com.airbnb_clone.config.dotenv.SubmoduleEnvironmentLoaderConfig;
import com.airbnb_clone.config.submodule.SubmodulePreInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class AirbnbCloneApplication {
	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(AirbnbCloneApplication.class);
		application.addInitializers(new SubmodulePreInitializer(), new SubmoduleEnvironmentLoaderConfig());
		application.run(args);
	}
}
