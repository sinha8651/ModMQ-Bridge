package com.application.close;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.application.close.links.service.BridgeExecutorService;
import com.application.close.modtcp.entity.TcpData;
import com.application.close.modtcp.service.ModService;
import com.application.close.modtcp.service.TcpDataService;
import com.application.close.mqtt.entity.MqttParam;
import com.application.close.mqtt.service.MqttParamService;
import com.application.close.mqtt.service.MqttService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@EnableScheduling
@SpringBootApplication
public class ModMqttBridgeApplication implements CommandLineRunner {

	private final Environment env;

	private final MqttParamService paramService;

	private final MqttService mqttService;

	private final TcpDataService tcpDataService;

	private final ModService modService;

	private final BridgeExecutorService bridgeService;

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

		log.info("MQTT Connection Process Started...");
		if (mqttService.getActiveMqtt().isEmpty()) {
			List<MqttParam> params = paramService.getAll();
			int mqttSuccess = 0;
			int mqttFailed = 0;

			for (MqttParam m : params) {
				try {
					mqttService.connect(m.getId());
					mqttSuccess++;
				} catch (Exception e) {
					mqttFailed++;
					log.error("Failed to connect MQTT parameter {}.", m.getId(), e.getMessage());
				}
			}

			log.info("MQTT Connection Process Completed. Success: {}, Failed: {}", mqttSuccess, mqttFailed);
		}

		log.info("MODBUS Connection Process Started...");
		if (modService.getActiveModbus().isEmpty()) {
			List<TcpData> tcpData = tcpDataService.getAll();
			int modbusSuccess = 0;
			int modbusFailed = 0;

			for (TcpData t : tcpData) {
				try {
					modService.connectToSlaveDevice(t.getId());
					modbusSuccess++;
				} catch (Exception e) {
					modbusFailed++;
					log.error("Failed to connect Modbus device {}.", t.getId(), e.getMessage());
				}
			}

			log.info("MODBUS Connection Process Completed. Success: {}, Failed: {}", modbusSuccess, modbusFailed);
		}

		log.info("Bridge cache loading started.");
		bridgeService.reloadCache();
		log.info("Bridge cache loaded successfully.");
	}

}
