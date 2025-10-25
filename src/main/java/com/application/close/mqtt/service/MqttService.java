package com.application.close.mqtt.service;

import com.application.close.mqtt.payload.MqttConnectResp;

public interface MqttService {

	MqttConnectResp connect(int mqttParamId); // Plain TCP connection

	MqttConnectResp connectWithAuth(int mqttParamId); // TCP + username/password

//	MqttConnectResp connectWithTLS(int mqttParamId); // TLS (SSL) connection
//
//	MqttConnectResp connectWithTLSAndAuth(int mqttParamId); // TLS + username/password
}
