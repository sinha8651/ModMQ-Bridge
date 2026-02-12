package com.application.close.mqtt.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Service;

import com.application.close.exception.BadRequestException;
import com.application.close.exception.ResourceNotFoundException;
import com.application.close.helper.MemoryBuffer;
import com.application.close.mqtt.entity.MqttParam;
import com.application.close.mqtt.repo.MqttParamRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class MqttServiceImpl implements MqttService {

	private final MqttParamRepo paramRepo;

	private final MemoryBuffer buffer;

	private static final String WILL_TOPIC = "status/lastwill";
	private static final String WILL_MESSAGE = "Mqtt client: %s disconnected unexpectedly.";

	@Override
	public String connect(int paramId) {
		MqttParam param = paramRepo.findById(paramId)
				.orElseThrow(() -> new ResourceNotFoundException("Mqtt param", "id", paramId));

		if (buffer.getMqttClient().containsKey(paramId)) {
			return reconnect(paramId);
		}

		if (param.isAuthEnabled()) {
			String mess = "Authentication must be disabled for a normal (non-auth) connection.";
			log.error(mess);
			throw new BadRequestException(mess);
		}

		MqttClient mqttClient = null;
		try {
			log.info("Attempting to connect MQTT client [clientId={}, url={}]", param.getClientId(), param.getUrl());

			mqttClient = new MqttClient(param.getUrl(), param.getClientId(), new MemoryPersistence());
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
			buffer.getMqttClient().put(paramId, mqttClient);

		} catch (MqttException e) {
			try {
				if (mqttClient != null)
					mqttClient.close();
			} catch (Exception ex) {
				log.warn("Failed to close MQTT client after error: {}", ex.getMessage());
			}
			String mess = String.format("MQTT connection failed for %s: %s", param.getUrl(), e.getMessage());
			log.error(mess);
			throw new BadRequestException(mess);
		}
		return String.format("Mqtt client connected for paramId: %s", paramId);
	}

	@Override
	public String connectWithAuth(int paramId) {
		MqttParam param = paramRepo.findById(paramId)
				.orElseThrow(() -> new ResourceNotFoundException("Mqtt param", "id", paramId));

		if (buffer.getMqttClient().containsKey(paramId)) {
			return reconnect(paramId);
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
			buffer.getMqttClient().put(paramId, mqttClient);

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
		return String.format("Mqtt client connected for paramId: %s", paramId);
	}

	@Override
	public String reconnect(int paramId) {
		MqttClient client = buffer.getMqttClient().get(paramId);
		try {
			if (client.isConnected()) {
				return "Mqtt client already active for paramId: " + paramId;
			} else {
				client.reconnect();
				return "Reconnected Mqtt client for paramId: " + paramId;
			}
		} catch (MqttException e) {
			throw new BadRequestException("Reconnect failed for paramId " + paramId + ": " + e.getMessage());
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
	public void disconnect(int paramId) {

		MqttClient client = buffer.getMqttClient().get(paramId);
		if (client == null) {
			throw new BadRequestException("No Mqtt Client found for paramId: " + paramId);
		}

		try {
			if (client.isConnected()) {
				client.disconnect();
			}

			log.info("MQTT client disconnected successfully for paramId: {}", paramId);

		} catch (MqttException e) {
			log.error("Failed to disconnect MQTT client with paramId: {} , message: {}", paramId, e.getMessage());
		}
	}

	@Override
	public List<MqttParam> getActiveMqtt() {
		List<MqttParam> activeList = new ArrayList<>();
		buffer.getMqttClient().entrySet().stream().filter(entry -> entry.getValue().isConnected())
				.map(entry -> paramRepo.findById(entry.getKey())).flatMap(Optional::stream).forEach(activeList::add);
		return activeList;
	}

}