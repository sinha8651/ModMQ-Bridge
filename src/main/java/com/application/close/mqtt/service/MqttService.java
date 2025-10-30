package com.application.close.mqtt.service;

public interface MqttService {

	String connect(int mqttParamId); // Plain TCP connection

	String connectWithAuth(int mqttParamId); // TCP + username/password

	String reconnect(int mqttParamId);

	void disconnect(int mqttParamId);

//	MqttConnectResp connectWithTLS(int mqttParamId); // TLS (SSL) connection
//
//	MqttConnectResp connectWithTLSAndAuth(int mqttParamId); // TLS + username/password
}
