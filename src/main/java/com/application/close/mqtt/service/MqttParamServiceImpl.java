package com.application.close.mqtt.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.application.close.exception.ResourceNotFoundException;
import com.application.close.mqtt.entity.MqttParam;
import com.application.close.mqtt.payload.MqttParamPayload;
import com.application.close.mqtt.payload.TopicPayload;
import com.application.close.mqtt.repo.MqttParamRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MqttParamServiceImpl implements MqttParamService {

	private final MqttParamRepo paramRepo;

	@Override
	public MqttParam create(MqttParamPayload paramPayload) {
		MqttParam param = new MqttParam();
		param.setName(paramPayload.getName());
		param.setClientId(paramPayload.getClientId());
		param.setUrl("tcp://" + paramPayload.getHost() + ":" + paramPayload.getPort());

		if (paramPayload.getUserName() != null) {
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
		param.setUrl("tcp://" + paramPayload.getHost() + ":" + paramPayload.getPort());
		param.setUserName(paramPayload.getUserName());
		param.setPassword(paramPayload.getPassword());
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
	public MqttParam addPublishTopics(TopicPayload topicPayload) {
		MqttParam param = getById(topicPayload.getMqttParamId());
		param.setPublishTopic(topicPayload.getTopics());
		return paramRepo.save(param);
	}

	@Override
	public MqttParam addSubscribeTopics(TopicPayload topicPayload) {
		MqttParam param = getById(topicPayload.getMqttParamId());
		param.setSubcribeTopic(topicPayload.getTopics());
		return paramRepo.save(param);
	}

}
