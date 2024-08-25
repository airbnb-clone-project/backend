package com.airbnb_clone;

import com.airbnb_clone.config.CustomContextInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {CustomContextInitializer.class})
@ActiveProfiles("test")
class AirbnbCloneApplicationTests {

	@Test
	void contextLoads() {
	}

}
