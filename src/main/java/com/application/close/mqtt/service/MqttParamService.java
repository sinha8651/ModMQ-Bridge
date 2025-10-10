package com.application.close.mqtt.service;

import java.util.List;

import com.application.close.mqtt.entity.MqttParam;
import com.application.close.mqtt.payload.MqttParamPayload;
import com.application.close.mqtt.payload.TopicPayload;

public interface MqttParamService {

	MqttParam create(MqttParamPayload paramPayload);

	MqttParam update(int paramId, MqttParamPayload paramPayload);

	void delete(int paramId);

	MqttParam getById(int paramId);

	List<MqttParam> getAll();

	MqttParam addPublishTopics(TopicPayload topicPayload);

	MqttParam addSubscribeTopics(TopicPayload topicPayload);

	void updateConnectionStatus(int paramId, boolean status);

}
