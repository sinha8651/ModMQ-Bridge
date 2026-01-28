package com.application.close;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ModMqttBridgeApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ModMqttBridgeApplication.class, args);

	}

	@Override
	public void run(String... args) throws Exception {
		// TODO document why this method is empty
	}

}
