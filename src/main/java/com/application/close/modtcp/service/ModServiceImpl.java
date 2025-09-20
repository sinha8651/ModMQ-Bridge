package com.application.close.modtcp.service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.stereotype.Service;

import com.application.close.exception.BadRequestException;
import com.application.close.helper.MemoryBuffer;
import com.application.close.modtcp.entity.TcpData;
import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.tcp.TcpParameters;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ModServiceImpl implements ModService {

	private final TcpDataService tcpDataService;

	private final MemoryBuffer buffer;

	@Override
	public String connectToSlaveDevice(Integer tcpDataId) {
	    if (tcpDataId == null) {
	        throw new BadRequestException("tcpDataId cannot be null.");
	    }

	    // If already in buffer, reconnect instead of duplicating
	    if (buffer.getModbusMaster().containsKey(tcpDataId)) {
	        return reconnect(tcpDataId);
	    }

	    TcpData tcpData = tcpDataService.getById(tcpDataId);
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
	public String reconnect(Integer tcpDataId) {
	    ModbusMaster modbusMaster = buffer.getModbusMaster().get(tcpDataId);

	    if (modbusMaster == null) {
	        throw new BadRequestException("No existing Modbus master found for tcpDataId: " + tcpDataId);
	    }

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
	public String disconnect(Integer tcpDataId) {
	    ModbusMaster modbusMaster = buffer.getModbusMaster().get(tcpDataId);

	    if (modbusMaster == null) {
	        throw new BadRequestException(
	                String.format("No Modbus master found for tcpDataId: %s", tcpDataId));
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

}
