package com.application.close.links.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.close.links.entity.ModMqttLinks;
import com.application.close.links.service.ModMqttLinksService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "ModMqttLinks Controller", description = "Operations to link or unlink Modbus TCP clients with MQTT parameters")
@RestController
@RequestMapping("/api/v1/links")
@RequiredArgsConstructor
public class ModMqttLinksController {

	private final ModMqttLinksService linksService;

	@Operation(summary = "Link Modbus TCP with MQTT Param", description = "Creates a mapping between a Modbus TCP client and an MQTT parameter.")
	@PostMapping("/connect/tcpId/{tcpId}/paramId/{paramId}")
	public ResponseEntity<String> connectModToMqtt(@PathVariable int tcpId, @PathVariable int paramId) {

		linksService.connectModToMqtt(tcpId, paramId);
		return ResponseEntity.ok("Linked Modbus TCP ID " + tcpId + " with MQTT Param ID " + paramId + " successfully.");
	}

	@Operation(summary = "Remove Modbus TCP–MQTT associations", description = "Unlinks Modbus TCP from all associated MQTT parameters and sets their values to zero")
	@DeleteMapping("/disconnect/tcpId/{tcpId}")
	public ResponseEntity<String> disconnectModFromMqtt(@PathVariable int tcpId) {
		linksService.disconnectModFromMqtt(tcpId);
		return ResponseEntity.ok("Unlinked Modbus TCP ID " + tcpId + " from its associated MQTT Param successfully.");
	}

	@Operation(summary = "Get all Modbus to MQTT links", description = "Retrieves all configured links between Modbus and MQTT")
	@GetMapping
	public ResponseEntity<List<ModMqttLinks>> getAll() {
		List<ModMqttLinks> links = linksService.getAllLinks();
		return ResponseEntity.ok(links);
	}

}
