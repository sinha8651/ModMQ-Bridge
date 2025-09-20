package com.application.close.modtcp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.close.modtcp.service.ModService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Modbus Service", description = "APIs for managing connections and operations with Modbus slave devices")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/mod")
public class ModServiceController {

	private final ModService modService;

	@Operation(summary = "Connect with a Modbus slave", description = "Connects and establishes communication with a slave using pre-existing slave data")
	@PostMapping("/tcpData/{tcpDataId}/connect")
	public ResponseEntity<String> connectToSlave(@PathVariable Integer tcpDataId) {
		String message = modService.connectToSlaveDevice(tcpDataId);
		return ResponseEntity.ok(message);
	}

	@Operation(summary = "Disconnect from a Modbus slave", description = "Disconnects and terminates communication with a slave using pre-existing slave data")
	@PostMapping("/tcpData/{tcpDataId}/disconnect")
	public ResponseEntity<String> disconnect(@PathVariable Integer tcpDataId) {
		String message = modService.disconnect(tcpDataId);
		return ResponseEntity.ok(message);
	}

}
