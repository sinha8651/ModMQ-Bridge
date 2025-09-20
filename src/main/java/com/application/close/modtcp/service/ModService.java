package com.application.close.modtcp.service;

public interface ModService {

	String connectToSlaveDevice(Integer tcpDataId);

	String reconnect(Integer tcpDataId);

	String disconnect(Integer tcpDataId);

}
