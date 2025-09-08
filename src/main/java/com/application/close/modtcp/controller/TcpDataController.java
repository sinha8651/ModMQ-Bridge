package com.application.close.modtcp.controller;

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

import com.application.close.modtcp.entity.TcpData;
import com.application.close.modtcp.payload.TcpPayload;
import com.application.close.modtcp.service.TcpDataService;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "TCP Data", description = "Operations related to TCP data management")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tcpData")
public class TcpDataController {

	private final TcpDataService dataService;

	@Operation(summary = "Create a new TCP data entry", description = "Adds a new TCP data record to the system.")
	@PostMapping
	public ResponseEntity<TcpData> create(@Valid @RequestBody TcpPayload tcp) {
		TcpData tcpData = dataService.create(tcp);
		return ResponseEntity.status(HttpStatus.CREATED).body(tcpData);
	}

	@Operation(summary = "Update an existing TCP data entry", description = "Updates details of a specific TCP data by its ID.")
	@PutMapping("/{tcpDataId}")
	public ResponseEntity<TcpData> update(@PathVariable Integer tcpDataId, @Valid @RequestBody TcpPayload tcp) {
		TcpData updatedData = dataService.update(tcpDataId, tcp);
		return ResponseEntity.ok(updatedData);
	}

	@Operation(summary = "Get TCP data by ID", description = "Fetches a TCP data record by its ID.")
	@GetMapping("/{tcpDataId}")
	public ResponseEntity<TcpData> getById(@PathVariable Integer tcpDataId) {
		TcpData tcpData = dataService.getById(tcpDataId);
		return ResponseEntity.ok(tcpData);
	}

	@Operation(summary = "Get all TCP data entries", description = "Retrieves all TCP data records.")
	@GetMapping
	public ResponseEntity<List<TcpData>> getAll() {
		List<TcpData> allData = dataService.getAll();
		return ResponseEntity.ok(allData);
	}

	@Operation(summary = "Delete TCP data by ID", description = "Removes a TCP data record by its ID.")
	@DeleteMapping("/{tcpDataId}")
	public ResponseEntity<Void> deleteById(@PathVariable Integer tcpDataId) {
		dataService.delete(tcpDataId);
		return ResponseEntity.noContent().build();
	}
}
