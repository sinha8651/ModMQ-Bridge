package com.application.close.modtcp.service;

public interface ModService {

	String connectToSlaveDevice(int tcpDataId);

	String reconnect(int tcpDataId);

	String disconnect(int tcpDataId);

	boolean[] readCoils(int tcpDataId, int slaveId, int offset, int quantity);

	boolean[] readDiscreteInputs(int tcpDataId, int slaveId, int offset, int quantity);

	int[] readHoldingRegisters(int tcpDataId, int slaveId, int offset, int quantity);

	int[] readInputRegisters(int tcpDataId, int slaveId, int offset, int quantity);

	void writeSingleCoil(int tcpDataId, int slaveId, int offset, boolean value);

	void writeMultipleCoils(int tcpDataId, int slaveId, int offset, boolean[] values);

	void writeSingleRegister(int tcpDataId, int slaveId, int offset, int value);

	void writeMultipleRegisters(int tcpDataId, int slaveId, int offset, int[] values);

}
