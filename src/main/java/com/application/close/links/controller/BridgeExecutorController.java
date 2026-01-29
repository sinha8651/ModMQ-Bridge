package com.application.close.links.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.close.links.entity.BridgeExecutor;
import com.application.close.links.payload.BridgeExecutorPayload;
import com.application.close.links.service.BridgeExecutorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Bridge Executor Controller", description = "Links the Opration done with modbus to mqtt")
@RestController
@RequestMapping("/api/v1/bridge")
@RequiredArgsConstructor
public class BridgeExecutorController {

	private final BridgeExecutorService bridgeExecutorService;

	@Operation(summary = "Create bridge executor")
	@PostMapping
	public ResponseEntity<BridgeExecutor> createBridge(@RequestBody @Valid BridgeExecutorPayload payload) {

		BridgeExecutor executor = bridgeExecutorService.createBridge(payload);
		return ResponseEntity.status(HttpStatus.CREATED).body(executor);
	}

	@Operation(summary = "Update bridge executor by id")
	@PutMapping("/id/{id}")
	public ResponseEntity<BridgeExecutor> updateBridge(@PathVariable int id,
			@RequestBody @Valid BridgeExecutorPayload payload) {

		BridgeExecutor executor = bridgeExecutorService.updateBridge(id, payload);
		return ResponseEntity.ok(executor);
	}

	@Operation(summary = "Get bridge executor by id")
	@GetMapping("/id/{id}")
	public ResponseEntity<BridgeExecutor> getById(@PathVariable int id) {

		return ResponseEntity.ok(bridgeExecutorService.getById(id));
	}

	@Operation(summary = "Get bridge executors by tcp data id")
	@GetMapping("/tcpdata/{tcpDataId}")
	public ResponseEntity<List<BridgeExecutor>> getByTcpDataId(@PathVariable int tcpDataId) {

		return ResponseEntity.ok(bridgeExecutorService.getByTcpDataId(tcpDataId));
	}

	@Operation(summary = "Get all bridge executors")
	@GetMapping
	public ResponseEntity<List<BridgeExecutor>> getAll() {

		return ResponseEntity.ok(bridgeExecutorService.getAll());
	}

	@Operation(summary = "Delete bridge executor by id")
	@DeleteMapping("/id/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable int id) {

		bridgeExecutorService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

}
