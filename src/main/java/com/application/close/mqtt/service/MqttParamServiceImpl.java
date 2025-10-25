package com.application.close.mqtt.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Service;

import com.application.close.exception.BadRequestException;
import com.application.close.exception.ResourceNotFoundException;
import com.application.close.helper.MemoryBuffer;
import com.application.close.mqtt.entity.MqttParam;
import com.application.close.mqtt.payload.MqttParamPayload;
import com.application.close.mqtt.payload.TopicPayload;
import com.application.close.mqtt.repo.MqttParamRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class MqttParamServiceImpl implements MqttParamService {

	private final MqttParamRepo paramRepo;

	private final MemoryBuffer buffer;

	@Override
	public MqttParam create(MqttParamPayload paramPayload) {
		MqttParam param = new MqttParam();
		param.setName(paramPayload.getName());
		param.setClientId(paramPayload.getClientId());
		param.setUrl("tcp://" + paramPayload.getHost() + ":1883");

		if (paramPayload.getUserName() == null) {
			param.setAuthEnabled(false);
			param.setUserName(null);
			param.setPassword(null);
		} else {
			param.setAuthEnabled(true);
			param.setUserName(paramPayload.getUserName());
			param.setPassword(paramPayload.getPassword());
		}

		param.setConnectTimeout(paramPayload.getConnectTimeout());
		param.setKeepAlive(paramPayload.getKeepAlive());
		param.setAutoReconnect(paramPayload.isAutoReconnect());
		param.setCleanStart(paramPayload.isCleanStart());
		param.setQos(paramPayload.getQos());
		param.setConnected(false);
		return paramRepo.save(param);
	}

	@Override
	public MqttParam update(int paramId, MqttParamPayload paramPayload) {
		MqttParam param = getById(paramId);
		param.setName(paramPayload.getName());
		param.setClientId(paramPayload.getClientId());
		param.setUrl("tcp://" + paramPayload.getHost() + ":1883");

		if (paramPayload.getUserName() == null) {
			param.setAuthEnabled(false);
			param.setUserName(null);
			param.setPassword(null);
		} else {
			param.setAuthEnabled(true);
			param.setUserName(paramPayload.getUserName());
			param.setPassword(paramPayload.getPassword());
		}

		param.setConnectTimeout(paramPayload.getConnectTimeout());
		param.setKeepAlive(paramPayload.getKeepAlive());
		param.setAutoReconnect(paramPayload.isAutoReconnect());
		param.setCleanStart(paramPayload.isCleanStart());
		param.setQos(paramPayload.getQos());
		param.setConnected(false);
		return paramRepo.save(param);
	}

	@Override
	public void delete(int paramId) {
		MqttParam param = getById(paramId);
		paramRepo.delete(param);
	}

	@Override
	public MqttParam getById(int paramId) {
		MqttParam param = paramRepo.findById(paramId)
				.orElseThrow(() -> new ResourceNotFoundException("Mqtt parameters", "id", paramId));
		return param;
	}

	@Override
	public List<MqttParam> getAll() {
		return paramRepo.findAll();
	}

	@Override
	public void updateConnectionStatus(int paramId, boolean status) {
		MqttParam param = getById(paramId);
		param.setConnected(status);
		paramRepo.save(param);
	}

	@Override
	public MqttParam addPublishTopics(TopicPayload topicPayload) {
		MqttParam param = getById(topicPayload.getMqttParamId());
		param.setPublishTopics(topicPayload.getTopics());
		return paramRepo.save(param);
	}

	@Override
	public MqttParam addSubscribeTopics(TopicPayload topicPayload) {
		MqttParam param = getById(topicPayload.getMqttParamId());
		param.setSubscribeTopics(topicPayload.getTopics());
		return paramRepo.save(param);
	}

	@Override
	public MqttParam addSingleSubscribeTopic(int paramId, String topic) {
		MqttParam param = getById(paramId);

		if (param.getSubscribeTopics() == null) {
			param.setSubscribeTopics(new ArrayList<>());
		}

		if (param.getSubscribeTopics().contains(topic)) {
			throw new BadRequestException("Topic already exists in subscription list: " + topic);
		}

		try {
			if (buffer.getMqttClient().containsKey(paramId))
				buffer.getMqttClient().get(paramId).subscribe(topic);
		} catch (MqttException e) {
			log.info("Failed to subscribe MQTT client to topic: " + topic + "message: " + e.getMessage());
		}

		param.getSubscribeTopics().add(topic);

		return paramRepo.save(param);

	}

	@Override
	public MqttParam addSinglePublishTopic(int paramId, String topic) {
		MqttParam param = getById(paramId);

		if (param.getPublishTopics() == null) {
			param.setPublishTopics(new ArrayList<>());
		}

		if (param.getPublishTopics().contains(topic)) {
			throw new BadRequestException("Topic already exists in publish list: " + topic);
		}

		param.getPublishTopics().add(topic);

		return paramRepo.save(param);
	}

	@Override
	public MqttParam removeSubscribeTopic(int paramId, String topic) {
		MqttParam param = getById(paramId);

		if (param.getSubscribeTopics() == null) {
			throw new BadRequestException("No Subscribe Topics  exists");
		}

		if (!param.getSubscribeTopics().contains(topic)) {
			throw new BadRequestException("Topic not exists in subscription list: " + topic);
		}

		try {
			if (buffer.getMqttClient().containsKey(paramId))
				buffer.getMqttClient().get(paramId).unsubscribe(topic);
		} catch (MqttException e) {
			log.info("Failed to subscribe MQTT client to topic: " + topic + "message: " + e.getMessage());
		}

		param.getSubscribeTopics().remove(topic);

		return paramRepo.save(param);
	}

	@Override
	public MqttParam removePublishTopic(int paramId, String topic) {
		MqttParam param = getById(paramId);

		if (param.getPublishTopics() == null || param.getPublishTopics().isEmpty()) {
			throw new BadRequestException("No publish topics exist for this MQTT parameter");
		}

		if (!param.getPublishTopics().contains(topic)) {
			throw new BadRequestException("Topic does not exist in publish list: " + topic);
		}

		param.getPublishTopics().remove(topic);

		return paramRepo.save(param);
	}

}
