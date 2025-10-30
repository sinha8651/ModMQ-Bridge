package com.application.close.mqtt.service;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import com.application.close.exception.BadRequestException;
import com.application.close.helper.MemoryBuffer;
import com.application.close.mqtt.entity.MqttParam;
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
	public String connect(int mqttParamId) {
		MqttParam param = paramService.getById(mqttParamId);

		if (buffer.getMqttClient().containsKey(mqttParamId)) {
			return reconnect(mqttParamId);
		}

		if (param.isAuthEnabled()) {
			throw new BadRequestException("Authentication must be disabled for a normal (non-auth) connection.");
		}

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
				}

				@Override
				public void connectComplete(boolean reconnect, String serverURI) {
					if (reconnect) {
						log.info("MQTT client reconnected successfully to {}", serverURI);
					} else {
						log.info("MQTT client connected for the first time to {}", serverURI);
					}
				}
			});

			mqttClient.connect(getMqttConnectOptions(param));
			buffer.getMqttClient().put(mqttParamId, mqttClient);

		} catch (MqttException e) {
			try {
				if (mqttClient != null)
					mqttClient.close();
			} catch (Exception ex) {
				log.warn("Failed to close MQTT client after error: {}", ex.getMessage());
			}
			throw new BadRequestException(
					String.format("MQTT connection failed for %s: %s", param.getUrl(), e.getMessage()));
		}
		return String.format("Mqtt client connected for paramId: %s", mqttParamId);
	}

	@Override
	public String connectWithAuth(int mqttParamId) {
		MqttParam param = paramService.getById(mqttParamId);

		if (buffer.getMqttClient().containsKey(mqttParamId)) {
			return reconnect(mqttParamId);
		}

		if (!param.isAuthEnabled())
			throw new BadRequestException("Auth must be enable for auth connection.");

		if (param.getUserName() == null || param.getPassword() == null)
			throw new BadRequestException("Username or password missing for Auth connection.");

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
				}

				@Override
				public void connectComplete(boolean reconnect, String serverURI) {
					if (reconnect) {
						log.info("MQTT client reconnected successfully to {}", serverURI);
					} else {
						log.info("MQTT client connected for the first time to {}", serverURI);
					}

				}
			});

			MqttConnectOptions mqttOptions = getMqttConnectOptions(param);
			mqttOptions.setUserName(param.getUserName());
			mqttOptions.setPassword(param.getPassword().toCharArray());

			mqttClient.connect(mqttOptions);
			buffer.getMqttClient().put(mqttParamId, mqttClient);

		} catch (MqttException e) {

			try {
				if (mqttClient != null)
					mqttClient.close();
			} catch (Exception ex) {
				log.warn("Failed to close MQTT client after error: {}", ex.getMessage());
			}

			throw new BadRequestException(
					String.format("MQTT connection failed for %s: %s", param.getUrl(), e.getMessage()));
		}
		return String.format("Mqtt client connected for paramId: %s", mqttParamId);
	}

	@Override
	public String reconnect(int mqttParamId) {
		MqttClient client = buffer.getMqttClient().get(mqttParamId);
		try {
			if (client.isConnected()) {
				return "Mqtt client already active for mqttParamId: " + mqttParamId;
			} else {
				client.reconnect();
				return "Reconnected Mqtt client for mqttParamId: " + mqttParamId;
			}
		} catch (MqttException e) {
			throw new BadRequestException("Reconnect failed for mqttParamId " + mqttParamId + ": " + e.getMessage());
		}
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

		MqttClient client = buffer.getMqttClient().get(mqttParamId);
		if (client == null) {
			throw new BadRequestException("No Mqtt Client found for mqttParamId: " + mqttParamId);
		}

		try {
			if (client.isConnected()) {
				client.disconnect();
			}

			log.info("MQTT client disconnected successfully for paramId: {}", mqttParamId);

		} catch (MqttException e) {
			log.error("Failed to disconnect MQTT client with paramId: {} , message: {}", mqttParamId, e.getMessage());
		}
	}

}