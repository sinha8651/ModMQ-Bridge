package com.application.close.modtcp.service;

public interface ModService {

	String connectToSlaveDevice(Integer tcpDataId);

	String reconnect(Integer tcpDataId);

	String disconnect(Integer tcpDataId);

	boolean[] readCoils(Integer tcpDataId, int slaveId, int offset, int quantity);

	boolean[] readDiscreteInputs(Integer tcpDataId, int slaveId, int offset, int quantity);

	int[] readHoldingRegisters(Integer tcpDataId, int slaveId, int offset, int quantity);

	int[] readInputRegisters(Integer tcpDataId, int slaveId, int offset, int quantity);

	void writeSingleCoil(Integer tcpDataId, int slaveId, int offset, boolean value);

	void writeMultipleCoils(Integer tcpDataId, int slaveId, int offset, boolean[] values);

	void writeSingleRegister(Integer tcpDataId, int slaveId, int offset, int value);

	void writeMultipleRegisters(Integer tcpDataId, int slaveId, int offset, int[] values);

}
