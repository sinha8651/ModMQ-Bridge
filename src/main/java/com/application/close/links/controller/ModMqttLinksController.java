package com.application.close.links.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.close.links.service.ModMqttLinksService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "ModMqttLinks Controller", description = "Operations to link or unlink Modbus TCP clients with MQTT parameters")
@RestController
@RequestMapping("/api/v1/links")
@RequiredArgsConstructor
public class ModMqttLinksController {

	private ModMqttLinksService linksService;

	@Operation(summary = "Link Modbus TCP with MQTT Param", description = "Creates a mapping between a Modbus TCP client and an MQTT parameter.")
	@PostMapping("/connect/modTcpId/{modTcpId}/mqttParamId/{mqttParamId}")
	public ResponseEntity<String> connectModToMqtt(@PathVariable int modTcpId, @PathVariable int mqttParamId) {

		linksService.connectModToMqtt(modTcpId, mqttParamId);
		return ResponseEntity
				.ok("Linked Modbus TCP ID " + modTcpId + " with MQTT Param ID " + mqttParamId + " successfully.");
	}

	@Operation(summary = "Unlink Modbus TCP from MQTT Param", description = "Removes the existing mapping between a Modbus TCP client and an MQTT parameter.")
	@DeleteMapping("/disconnect/modTcpId/{modTcpId}")
	public ResponseEntity<String> disconnectModFromMqtt(@PathVariable int modTcpId) {
		linksService.disconnectModFromMqtt(modTcpId);
		return ResponseEntity
				.ok("Unlinked Modbus TCP ID " + modTcpId + " from its associated MQTT Param successfully.");
	}

}
