package com.application.close.mqtt.service;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

import com.application.close.exception.BadRequestException;
import com.application.close.helper.MemoryBuffer;
import com.application.close.mqtt.entity.MqttParam;
import com.application.close.mqtt.payload.MqttConnectResp;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class MqttServiceImpl implements MqttService {

	private final MqttParamService paramService;

	private final MemoryBuffer buffer;

	private static final String WILL_TOPIC = "status/lastwill";
	private static final String WILL_MESSAGE = "Mqtt client: %s disconnected unexpectedly.";

	@Override
	public MqttConnectResp connect(int mqttParamId) {
		MqttParam param = paramService.getById(mqttParamId);

		if (param.isConnected())
			throw new BadRequestException("MQTT client already connected.");

		// Ensure Auth is disabled for normal connection
		if (param.isAuthEnabled())
			throw new BadRequestException(" Auth must be disabled for normal connection.");

		MqttConnectResp resp = new MqttConnectResp();
		resp.setClientId(param.getClientId());
		resp.setMqttUrl(param.getUrl());

		MqttClient mqttClient = null;
		try {
			mqttClient = new MqttClient(param.getUrl(), param.getClientId());
			mqttClient.connect(getMqttConnectOptions(param));

			param.setConnected(true);
			paramService.updateConnectionStatus(mqttParamId, true);
			buffer.getMqttClient().put(mqttParamId, mqttClient);

			resp.setConnected(true);
			log.info("MQTT client connected successfully: {}", param.getUrl());
		} catch (MqttException e) {
			resp.setConnected(false);
			log.error("MQTT connection failed for {}: {}", param.getUrl(), e.getMessage(), e);
		}
		return resp;
	}

	@Override
	public MqttConnectResp connectWithAuth(int mqttParamId) {
		MqttParam param = paramService.getById(mqttParamId);

		if (param.isConnected())
			throw new BadRequestException("MQTT client already connected.");

		if (!param.isAuthEnabled())
			throw new BadRequestException("Auth must be enable for auth connection.");

		if (param.getUserName() == null || param.getPassword() == null)
			throw new BadRequestException("Username or password missing for Auth connection.");

		MqttConnectResp resp = new MqttConnectResp();
		resp.setClientId(param.getClientId());
		resp.setMqttUrl(param.getUrl());

		MqttClient mqttClient = null;
		try {
			mqttClient = new MqttClient(param.getUrl(), param.getClientId());
			MqttConnectOptions mqttOptions = getMqttConnectOptions(param);
			mqttOptions.setUserName(param.getUserName());
			mqttOptions.setPassword(param.getPassword().toCharArray());

			mqttClient.connect(mqttOptions);

			param.setConnected(true);
			paramService.updateConnectionStatus(mqttParamId, true);
			buffer.getMqttClient().put(mqttParamId, mqttClient);

			resp.setConnected(true);
			log.info("MQTT client connected successfully: {}", param.getUrl());
		} catch (MqttException e) {
			resp.setConnected(false);
			log.error("MQTT connection failed for {}: {}", param.getUrl(), e.getMessage(), e);
		}
		return resp;
	}

	private MqttConnectOptions getMqttConnectOptions(MqttParam param) {
		MqttConnectOptions mqttOptions = new MqttConnectOptions();
		mqttOptions.setAutomaticReconnect(param.isAutoReconnect());
		mqttOptions.setCleanSession(param.isCleanStart());
		mqttOptions.setConnectionTimeout(param.getConnectTimeout());
		mqttOptions.setKeepAliveInterval(param.getKeepAlive());
		mqttOptions.setWill(WILL_TOPIC, String.format(WILL_MESSAGE, param.getClientId()).getBytes(), 1, false);
		return mqttOptions;
	}

}
