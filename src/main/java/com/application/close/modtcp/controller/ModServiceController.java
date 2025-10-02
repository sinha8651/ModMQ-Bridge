package com.application.close.modtcp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@Operation(summary = "Read Coils from Modbus slave", description = "Reads coil values (ON/OFF, read/write bits) from the given slave device using the pre-existing TCP connection.")
	@GetMapping("/tcpData/{tcpDataId}/readCoils")
	public ResponseEntity<boolean[]> readCoils(@PathVariable Integer tcpDataId, @RequestParam int slaveId,
			@RequestParam int offset, @RequestParam int quantity) {
		boolean[] coils = modService.readCoils(tcpDataId, slaveId, offset, quantity);
		return ResponseEntity.ok(coils);
	}

	@Operation(summary = "Read Discrete Inputs from Modbus slave", description = "Reads discrete input values (ON/OFF, read-only bits) from the given slave device using the pre-existing TCP connection.")
	@GetMapping("/tcpData/{tcpDataId}/discreteInputs")
	public ResponseEntity<boolean[]> readDiscreteInputs(@PathVariable Integer tcpDataId, @RequestParam int slaveId,
			@RequestParam int offset, @RequestParam int quantity) {
		boolean[] inputs = modService.readDiscreteInputs(tcpDataId, slaveId, offset, quantity);
		return ResponseEntity.ok(inputs);
	}

	@Operation(summary = "Read Holding Registers from Modbus slave", description = "Reads holding register values (read/write registers) from the given slave device using the pre-existing TCP connection.")
	@GetMapping("/tcpData/{tcpDataId}/holdingRegisters")
	public ResponseEntity<int[]> readHoldingRegisters(@PathVariable Integer tcpDataId, @RequestParam int slaveId,
			@RequestParam int offset, @RequestParam int quantity) {

		int[] registers = modService.readHoldingRegisters(tcpDataId, slaveId, offset, quantity);
		return ResponseEntity.ok(registers);
	}

	@Operation(summary = "Read Input Registers from Modbus slave", description = "Reads input register values (read-only registers) from the given slave device using the pre-existing TCP connection.")
	@GetMapping("/tcpData/{tcpDataId}/inputRegisters")
	public ResponseEntity<int[]> readInputsRegisters(@PathVariable Integer tcpDataId, @RequestParam int slaveId,
			@RequestParam int offset, @RequestParam int quantity) {
		int[] registers = modService.readInputRegisters(tcpDataId, slaveId, offset, quantity);
		return ResponseEntity.ok(registers);

	}

	@Operation(summary = "Write Single Coil to Modbus slave", description = "Writes a single coil (ON/OFF) to the given slave device using the pre-existing TCP connection.")
	@PostMapping("/tcpData/{tcpDataId}/coils/single")
	public ResponseEntity<String> writeSingleCoil(@PathVariable Integer tcpDataId, @RequestParam int slaveId,
			@RequestParam int offset, @RequestParam boolean value) {

		modService.writeSingleCoil(tcpDataId, slaveId, offset, value);
		return ResponseEntity.ok(String.format("Successfully wrote coil at offset %d with value %b on slave %d", offset,
				value, slaveId));
	}

	@Operation(summary = "Write Multiple Coils to Modbus slave", description = "Writes multiple coils (ON/OFF) starting from the given offset to the slave device.")
	@PostMapping("/tcpData/{tcpDataId}/coils/multiple")
	public ResponseEntity<String> writeMultipleCoils(@PathVariable Integer tcpDataId, @RequestParam int slaveId,
			@RequestParam int offset, @RequestBody boolean[] values) {

		modService.writeMultipleCoils(tcpDataId, slaveId, offset, values);
		return ResponseEntity.ok(String.format("Successfully wrote %d coils starting at offset %d on slave %d",
				values.length, offset, slaveId));
	}

	@Operation(summary = "Write Single Register to Modbus slave", description = "Writes a single holding register value to the given slave device using the pre-existing TCP connection.")
	@PostMapping("/tcpData/{tcpDataId}/holdingRegisters/single")
	public ResponseEntity<String> writeSingleRegister(@PathVariable Integer tcpDataId, @RequestParam int slaveId,
			@RequestParam int offset, @RequestParam int value) {

		modService.writeSingleRegister(tcpDataId, slaveId, offset, value);
		return ResponseEntity.ok(String.format("Successfully wrote register at offset %d with value %d on slave %d",
				offset, value, slaveId));
	}

	@Operation(summary = "Write Multiple Registers to Modbus slave", description = "Writes multiple holding register values starting from the given offset to the slave device.")
	@PostMapping("/tcpData/{tcpDataId}/holdingRegisters/multiple")
	public ResponseEntity<String> writeMultipleRegisters(@PathVariable Integer tcpDataId, @RequestParam int slaveId,
			@RequestParam int offset, @RequestBody int[] values) {

		modService.writeMultipleRegisters(tcpDataId, slaveId, offset, values);
		return ResponseEntity.ok(String.format("Successfully wrote %d registers starting at offset %d on slave %d",
				values.length, offset, slaveId));
	}

}
