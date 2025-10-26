package com.application.close.mqtt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.close.mqtt.payload.MqttConnectResp;
import com.application.close.mqtt.service.MqttService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "MQTT Controller", description = "Provides operations to connect, authenticate, and disconnect MQTT clients.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mqtt")
public class MqttController {

	private final MqttService mqttService;

	@Operation(summary = "Connect to MQTT broker", description = "Establishes a connection between the MQTT client and the configured broker for the provided parameter ID.")
	@PostMapping("/connect/mqttparam/{paramId}")
	public ResponseEntity<MqttConnectResp> connect(@PathVariable Integer paramId) {
		MqttConnectResp response = mqttService.connect(paramId);
		return ResponseEntity.accepted().body(response);
	}

	@Operation(summary = "Connect to MQTT broker with authentication", description = "Connects the MQTT client to the broker using stored authentication credentials such as username/password")
	@PostMapping("/connect/mqttparam/{paramId}/auth")
	public ResponseEntity<MqttConnectResp> connectWithAuth(@PathVariable Integer paramId) {
		MqttConnectResp response = mqttService.connectWithAuth(paramId);
		return ResponseEntity.accepted().body(response);
	}

	@Operation(summary = "Disconnect MQTT client", description = "Terminates the active MQTT client session for the given parameter ID.")
	@DeleteMapping("/disconnect/mqttparam/{paramId}")
	public ResponseEntity<String> disconnect(@PathVariable Integer paramId) {
		mqttService.disconnect(paramId);
		return ResponseEntity.ok("MQTT client disconnected successfully.");
	}
}
