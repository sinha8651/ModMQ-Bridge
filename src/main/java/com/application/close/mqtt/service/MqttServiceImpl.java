package com.application.close.mqtt.service;

import org.springframework.stereotype.Service;

import com.application.close.mqtt.payload.MqttConnectResp;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MqttServiceImpl implements MqttService {

	@Override
	public MqttConnectResp connect(int mqttParamId) {

		return null;
	}

	@Override
	public MqttConnectResp connectWithAuth(int mqttParamId) {

		return null;
	}

	@Override
	public MqttConnectResp connectWithTLS(int mqttParamId) {

		return null;
	}

	@Override
	public MqttConnectResp connectWithTLSAndAuth(int mqttParamId) {

		return null;
	}

}
