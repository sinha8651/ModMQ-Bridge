package com.application.close;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@EnableScheduling
@SpringBootApplication
public class ModMqttBridgeApplication implements CommandLineRunner {

	private final Environment env;

	public static void main(String[] args) {
		SpringApplication.run(ModMqttBridgeApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception {
		log.info("""

				============================================================
				        MODBUS MQTT BRIDGE SERVICE
				============================================================
				 Application  : {}
				 Version      : {}
				 Environment  : {}
				 Status       : STARTED
				------------------------------------------------------------
				 Features:
				  -> Modbus TCP Data Acquisition
				  -> MQTT Publish Modbus Data
				  -> TLS Communication
				  -> Reliable Data Bridging
				============================================================
				""", env.getProperty("spring.application.name"), env.getProperty("spring.application.version"),
				env.getProperty("spring.profiles.active"));
	}

}
