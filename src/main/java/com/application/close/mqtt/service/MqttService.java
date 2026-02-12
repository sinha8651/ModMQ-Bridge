package com.application.close.mqtt.service;

import java.util.List;

import com.application.close.mqtt.entity.MqttParam;

public interface MqttService {

	String connect(int paramId); // Plain TCP connection

	String connectWithAuth(int paramId); // TCP + username/password

	String reconnect(int paramId);

	void disconnect(int paramId);

	List<MqttParam> getActiveMqtt();

}
