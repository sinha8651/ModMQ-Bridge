package com.application.close.modtcp.service;

import java.util.List;

import com.application.close.modtcp.entity.TcpData;

public interface ModService {

	String connectToSlaveDevice(int tcpId);

	String reconnect(int tcpId);

	String disconnect(int tcpId);

	List<TcpData> getActiveModbus();

	boolean[] readCoils(int tcpId, int slaveId, int offset, int quantity);

	boolean[] readDiscreteInputs(int tcpId, int slaveId, int offset, int quantity);

	int[] readHoldingRegisters(int tcpId, int slaveId, int offset, int quantity);

	int[] readInputRegisters(int tcpId, int slaveId, int offset, int quantity);

	void writeSingleCoil(int tcpId, int slaveId, int offset, boolean value);

	void writeMultipleCoils(int tcpId, int slaveId, int offset, boolean[] values);

	void writeSingleRegister(int tcpId, int slaveId, int offset, int value);

	void writeMultipleRegisters(int tcpId, int slaveId, int offset, int[] values);

}
