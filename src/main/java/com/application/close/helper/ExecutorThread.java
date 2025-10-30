package com.application.close.helper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.annotation.PreDestroy;

public class ExecutorThread {

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	

	@Scheduled(fixedRate = 18000)
	public void fetchingModbusData() {

	}

	@PreDestroy
	public void shutdownExecutor() {
		executor.shutdown();
	}

}
