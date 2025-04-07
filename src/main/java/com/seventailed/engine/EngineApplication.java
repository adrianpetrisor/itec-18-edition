package com.seventailed.engine;

import com.seventailed.engine.logger.EngineLogging;
import com.seventailed.engine.utils.EngineUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class EngineApplication {
	public static void main(String[] args) throws Exception {
		EngineLogging.initializeColors();
		EngineLogging.initializeStream();

		EngineLogging.clearScreen();
		EngineLogging.log("&8[&2SevenTailed&8]&r Time: " + EngineUtils.getLocalTimeDate());

		SpringApplication.run(EngineApplication.class, args);
	}
}
