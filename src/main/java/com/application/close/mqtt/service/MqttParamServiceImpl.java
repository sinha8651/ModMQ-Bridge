package com.application.close.mqtt.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.application.close.exception.BadRequestException;
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

		paramPayload.setSslEnabled(paramPayload.isSslEnabled());
		if (paramPayload.isSslEnabled())
			param.setUrl("tcp://" + paramPayload.getHost() + ":" + paramPayload.getPort());
		else
			param.setUrl("ssl://" + paramPayload.getHost() + ":" + paramPayload.getPort());

		paramPayload.setAuthEnabled(paramPayload.isAuthEnabled());
		if (paramPayload.isAuthEnabled()) {
			param.setUsername(paramPayload.getUsername());
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

		paramPayload.setSslEnabled(paramPayload.isSslEnabled());
		if (paramPayload.isSslEnabled())
			param.setUrl("ssl://" + paramPayload.getHost() + ":" + paramPayload.getPort());
		else
			param.setUrl("tcp://" + paramPayload.getHost() + ":" + paramPayload.getPort());

		paramPayload.setAuthEnabled(paramPayload.isAuthEnabled());
		if (paramPayload.isAuthEnabled()) {
			String username = paramPayload.getUsername();
			String password = paramPayload.getPassword();

			if (username == null || username.isBlank() || password == null || password.isBlank()) {
				throw new BadRequestException("Username and password cannot be null or empty");
			}

			param.setUsername(username);
			param.setPassword(password);
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
	public void updateConnectionStatus(int paramId, boolean status) {
		MqttParam param = getById(paramId);
		param.setConnected(status);
		paramRepo.save(param);
	}

}
