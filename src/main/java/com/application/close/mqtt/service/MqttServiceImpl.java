package com.application.close.mqtt.service;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import com.application.close.exception.BadRequestException;
import com.application.close.exception.ResourceNotFoundException;
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

		if (param == null) {
			throw new ResourceNotFoundException("MqttParam", "mqttParamId", mqttParamId);
		}

		if (param.isConnected()) {
			throw new BadRequestException("MQTT client is already connected for ID: " + mqttParamId);
		}

		if (param.isAuthEnabled()) {
			throw new BadRequestException("Authentication must be disabled for a normal (non-auth) connection.");
		}

		MqttConnectResp resp = new MqttConnectResp();
		resp.setClientId(param.getClientId());
		resp.setMqttUrl(param.getUrl());

		MqttClient mqttClient = null;
		try {
			log.info("Attempting to connect MQTT client [clientId={}, url={}]", param.getClientId(), param.getUrl());

			mqttClient = new MqttClient(param.getUrl(), param.getClientId());
			mqttClient.setCallback(new MqttCallbackExtended() {

				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					log.info("Message arrived on topic [{}]: {}", topic, new String(message.getPayload()));
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {
					log.debug("Message delivery complete for clientId={}", param.getClientId());
				}

				@Override
				public void connectionLost(Throwable cause) {
					log.warn("MQTT connection lost for clientId={}: {}", param.getClientId(), cause.getMessage());
					paramService.updateConnectionStatus(mqttParamId, false);
				}

				@Override
				public void connectComplete(boolean reconnect, String serverURI) {
					if (reconnect) {
						log.info("MQTT client reconnected successfully to {}", serverURI);
					} else {
						log.info("MQTT client connected for the first time to {}", serverURI);
					}
					paramService.updateConnectionStatus(mqttParamId, true);

				}
			});

			mqttClient.connect(getMqttConnectOptions(param));

			mqttClient.subscribe(param.getSubscribeTopics().stream().toArray(String[]::new));
			buffer.getMqttClient().put(mqttParamId, mqttClient);

			resp.setConnected(true);
		} catch (MqttException e) {
			resp.setConnected(false);
			log.error("MQTT connection failed for {}: {}", param.getUrl(), e.getMessage(), e);
		}
		return resp;
	}

	@Override
	public MqttConnectResp connectWithAuth(int mqttParamId) {
		MqttParam param = paramService.getById(mqttParamId);

		if (param == null) {
			throw new ResourceNotFoundException("MqttParam", "mqttParamId", mqttParamId);
		}

		if (param.isConnected()) {
			throw new BadRequestException("MQTT client is already connected for ID: " + mqttParamId);
		}

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
			mqttClient.setCallback(new MqttCallbackExtended() {

				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					log.info("Message arrived on topic [{}]: {}", topic, new String(message.getPayload()));
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {
					log.debug("Message delivery complete for clientId={}", param.getClientId());
				}

				@Override
				public void connectionLost(Throwable cause) {
					log.warn("MQTT connection lost for clientId={}: {}", param.getClientId(), cause.getMessage());
					paramService.updateConnectionStatus(mqttParamId, false);
				}

				@Override
				public void connectComplete(boolean reconnect, String serverURI) {
					if (reconnect) {
						log.info("MQTT client reconnected successfully to {}", serverURI);
					} else {
						log.info("MQTT client connected for the first time to {}", serverURI);
					}
					paramService.updateConnectionStatus(mqttParamId, true);

				}
			});

			MqttConnectOptions mqttOptions = getMqttConnectOptions(param);
			mqttOptions.setUserName(param.getUserName());
			mqttOptions.setPassword(param.getPassword().toCharArray());

			mqttClient.connect(mqttOptions);

			mqttClient.subscribe(param.getSubscribeTopics().stream().toArray(String[]::new));
			buffer.getMqttClient().put(mqttParamId, mqttClient);

			resp.setConnected(true);
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

	@Override
	public void disconnect(int mqttParamId) {
		MqttParam param = paramService.getById(mqttParamId);

		if (param == null)
			throw new ResourceNotFoundException("MqttParam", "mqttParamId", mqttParamId);

		if (!param.isConnected())
			throw new BadRequestException("MQTT client is not connected");

		 try {
		        if (buffer.getMqttClient().containsKey(mqttParamId)) {
		            buffer.getMqttClient().get(mqttParamId).disconnect();
		            buffer.getMqttClient().remove(mqttParamId);
		            log.info("MQTT client disconnected successfully for ID: {}", mqttParamId);
		        }
		    } catch (MqttException e) {
		        log.error("Failed to disconnect MQTT client with ID {}: {}", mqttParamId, e.getMessage());
		    }
	}

}