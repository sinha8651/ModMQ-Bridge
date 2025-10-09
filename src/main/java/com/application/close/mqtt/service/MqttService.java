package com.application.close.mqtt.service;

import com.application.close.mqtt.payload.MqttConnectResp;

public interface MqttService {

	MqttConnectResp connect(int mqttParamId);

	MqttConnectResp connectWithUserName(int mqttParamId);

}
