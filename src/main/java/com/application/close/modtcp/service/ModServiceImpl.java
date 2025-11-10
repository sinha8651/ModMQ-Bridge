package com.application.close.modtcp.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.application.close.exception.BadRequestException;
import com.application.close.exception.ModbusOperationException;
import com.application.close.helper.MemoryBuffer;
import com.application.close.modtcp.entity.TcpData;
import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusNumberException;
import com.intelligt.modbus.jlibmodbus.exception.ModbusProtocolException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ModServiceImpl implements ModService {

	/**
	 * =========================== MODBUS DATA MODEL (BASIC)
	 * ===========================
	 *
	 * Modbus organizes data into 4 distinct tables:
	 *
	 * 1. COILS (0xxxx) - Address Range: 00001 – 09999 (logical) - Access:
	 * Read/Write - Size: 1 bit - Typical Use: Digital outputs (ON/OFF, relays,
	 * LEDs) - Function Codes: 01 - Read Coils 05 - Write Single Coil 15 - Write
	 * Multiple Coils
	 *
	 * 2. DISCRETE INPUTS (1xxxx) - Address Range: 10001 – 19999 (logical) - Access:
	 * Read-only - Size: 1 bit - Typical Use: Digital inputs (switches, push
	 * buttons, sensors) - Function Codes: 02 - Read Discrete Inputs
	 *
	 * 3. INPUT REGISTERS (3xxxx) - Address Range: 30001 – 39999 (logical) - Access:
	 * Read-only - Size: 16-bit word - Typical Use: Analog inputs (temperature,
	 * voltage, current sensors) - Function Codes: 04 - Read Input Registers
	 *
	 * 4. HOLDING REGISTERS (4xxxx) - Address Range: 40001 – 49999 (logical) -
	 * Access: Read/Write - Size: 16-bit word - Typical Use: Analog outputs,
	 * parameters, configuration - Function Codes: 03 - Read Holding Registers 06 -
	 * Write Single Register 16 - Write Multiple Registers
	 *
	 * =========================== IMPORTANT NOTES =========================== -
	 * Addressing in Modbus specifications (e.g., 40001) is logical, but libraries
	 * like jlibmodbus use ZERO-BASED offsets. Example: 40001 → offset 0 40010 →
	 * offset 9
	 *
	 * - Coils & Discrete Inputs = 1-bit boolean values. - Input & Holding Registers
	 * = 16-bit unsigned ints (0–65535). - 32-bit (float, long) values = stored in
	 * TWO consecutive 16-bit registers. - Only COILS and HOLDING REGISTERS are
	 * writable.
	 */

	private final TcpDataService tcpDataService;

	private final MemoryBuffer buffer;

	@Override
	public String connectToSlaveDevice(int tcpDataId) {

		TcpData tcpData = tcpDataService.getById(tcpDataId);

		// If already in buffer, reconnect instead of duplicating
		if (buffer.getModbusMaster().containsKey(tcpDataId)) {
			return reconnect(tcpDataId);
		}

		TcpParameters tcpParameters = new TcpParameters();

		try {
			InetAddress address = InetAddress.getByName(tcpData.getHost());
			tcpParameters.setHost(address);
		} catch (UnknownHostException e) {
			throw new BadRequestException("Unknown Host: " + tcpData.getHost());
		}

		tcpParameters.setPort(tcpData.getPort());
		tcpParameters.setKeepAlive(tcpData.isKeepAlive());

		ModbusMaster modbusMaster = ModbusMasterFactory.createModbusMasterTCP(tcpParameters);
		Modbus.setAutoIncrementTransactionId(true);
		try {
			modbusMaster.connect();
		} catch (ModbusIOException e) {
			throw new BadRequestException("Failed to connect Modbus master: " + e.getMessage());
		}

		buffer.getModbusMaster().put(tcpDataId, modbusMaster);
		return String.format("Modbus master connected for tcpDataId: %s", tcpDataId);
	}

	@Override
	public String reconnect(int tcpDataId) {
		ModbusMaster modbusMaster = buffer.getModbusMaster().get(tcpDataId);
		try {
			if (!modbusMaster.isConnected()) {
				modbusMaster.connect();
				return "Reconnected Modbus master for tcpDataId: " + tcpDataId;
			} else {
				return "Modbus master already active for tcpDataId: " + tcpDataId;
			}
		} catch (ModbusIOException e) {
			throw new BadRequestException("Reconnect failed for tcpDataId " + tcpDataId + ": " + e.getMessage());
		}
	}

	@Override
	public String disconnect(int tcpDataId) {
		ModbusMaster modbusMaster = buffer.getModbusMaster().get(tcpDataId);
		if (modbusMaster == null) {
			throw new BadRequestException(String.format("No Modbus master found for tcpDataId: %s", tcpDataId));
		}

		try {
			if (modbusMaster.isConnected()) {
				modbusMaster.disconnect();
			}
			return String.format("Modbus master disconnected for tcpDataId: %s", tcpDataId);
		} catch (ModbusIOException e) {
			throw new BadRequestException(String.format(
					"Failed to disconnect Modbus master for tcpDataId: %s, message: %s", tcpDataId, e.getMessage()));
		}
	}

	@Override
	public boolean[] readCoils(int tcpDataId, int slaveId, int offset, int quantity) {
		ModbusMaster modbusMaster = getModbusMaster(tcpDataId);
		if (offset + quantity > 65536) {
			throw new ModbusOperationException("Offset + Quantity exceeds Modbus register limit");
		}

		try {
			return Arrays.copyOf(modbusMaster.readCoils(slaveId, offset, quantity), quantity);
		} catch (ModbusProtocolException | ModbusNumberException | ModbusIOException e) {
			throw new ModbusOperationException(
					String.format("Failed to read coils: slaveId: %s ,offset: %s and quantity: %s , message: %s",
							slaveId, offset, quantity, e.getMessage()));
		}
	}

	@Override
	public boolean[] readDiscreteInputs(int tcpDataId, int slaveId, int offset, int quantity) {
		ModbusMaster modbusMaster = getModbusMaster(tcpDataId);
		if (offset + quantity > 65536) {
			throw new ModbusOperationException("Offset + Quantity exceeds Modbus register limit");
		}

		try {
			return Arrays.copyOf(modbusMaster.readDiscreteInputs(slaveId, offset, quantity), quantity);
		} catch (ModbusProtocolException | ModbusNumberException | ModbusIOException e) {
			throw new ModbusOperationException(String.format(
					"Failed to read discrete inputs: slaveId: %s ,offset: %s and quantity: %s , message: %s", slaveId,
					offset, quantity, e.getMessage()));
		}
	}

	@Override
	public int[] readHoldingRegisters(int tcpDataId, int slaveId, int offset, int quantity) {
		ModbusMaster modbusMaster = getModbusMaster(tcpDataId);
		if (offset + quantity > 65536) {
			throw new ModbusOperationException("Offset + Quantity exceeds Modbus register limit");
		}

		try {
			return modbusMaster.readHoldingRegisters(slaveId, offset, quantity);
		} catch (ModbusProtocolException | ModbusNumberException | ModbusIOException e) {
			throw new ModbusOperationException(String.format(
					"Failed to read holding registers: slaveId: %s ,offset: %s and quantity: %s , message: %s", slaveId,
					offset, quantity, e.getMessage()));
		}
	}

	@Override
	public int[] readInputRegisters(int tcpDataId, int slaveId, int offset, int quantity) {
		ModbusMaster modbusMaster = getModbusMaster(tcpDataId);
		if (offset + quantity > 65536) {
			throw new ModbusOperationException("Offset + Quantity exceeds Modbus register limit");
		}

		try {
			return modbusMaster.readInputRegisters(slaveId, offset, quantity);
		} catch (ModbusProtocolException | ModbusNumberException | ModbusIOException e) {
			throw new ModbusOperationException(String.format(
					"Failed to read input registers: slaveId: %s ,offset: %s and quantity: %s , message: %s", slaveId,
					offset, quantity, e.getMessage()));
		}
	}

	@Override
	public void writeSingleCoil(int tcpDataId, int slaveId, int offset, boolean value) {

		ModbusMaster modbusMaster = getModbusMaster(tcpDataId);
		try {
			modbusMaster.writeSingleCoil(slaveId, offset, value);
		} catch (ModbusProtocolException | ModbusNumberException | ModbusIOException e) {
			throw new ModbusOperationException(
					String.format("Failed to write single coil: slaveId: %s ,offset: %s and value: %s , message: %s",
							slaveId, offset, value, e.getMessage()));
		}

	}

	@Override
	public void writeMultipleCoils(int tcpDataId, int slaveId, int offset, boolean[] values) {

		ModbusMaster modbusMaster = getModbusMaster(tcpDataId);
		try {
			modbusMaster.writeMultipleCoils(slaveId, offset, values);
		} catch (ModbusProtocolException | ModbusNumberException | ModbusIOException e) {
			throw new ModbusOperationException(
					String.format("Failed to write multiple coils: slaveId: %s ,offset: %s and value: %s , message: %s",
							slaveId, offset, values, e.getMessage()));
		}

	}

	@Override
	public void writeSingleRegister(int tcpDataId, int slaveId, int offset, int value) {

		ModbusMaster modbusMaster = getModbusMaster(tcpDataId);
		try {
			modbusMaster.writeSingleRegister(slaveId, offset, value);
		} catch (ModbusProtocolException | ModbusNumberException | ModbusIOException e) {
			throw new ModbusOperationException(String.format(
					"Failed to write single registers: slaveId: %s ,offset: %s and value: %s , message: %s", slaveId,
					offset, value, e.getMessage()));
		}

	}

	@Override
	public void writeMultipleRegisters(int tcpDataId, int slaveId, int offset, int[] values) {

		ModbusMaster modbusMaster = getModbusMaster(tcpDataId);
		try {
			modbusMaster.writeMultipleRegisters(slaveId, offset, values);
		} catch (ModbusProtocolException | ModbusNumberException | ModbusIOException e) {
			throw new ModbusOperationException(String.format(
					"Failed to write multiple registers: slaveId: %s ,offset: %s and value: %s , message: %s", slaveId,
					offset, values, e.getMessage()));
		}
	}

	private ModbusMaster getModbusMaster(int tcpDataId) {
		ModbusMaster master = buffer.getModbusMaster().get(tcpDataId);
		if (master == null) {
			throw new ModbusOperationException("No existing Modbus master found for tcpDataId: " + tcpDataId);
		}
		return master;
	}

}
