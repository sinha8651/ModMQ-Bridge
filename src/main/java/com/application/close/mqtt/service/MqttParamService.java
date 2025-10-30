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

//	void updateConnectionStatus(int paramId, boolean status);

	boolean existsById(int paramId);

	MqttParam addPublishTopics(TopicPayload topicPayload);

	MqttParam addSubscribeTopics(TopicPayload topicPayload);

	MqttParam addSingleSubscribeTopic(int paramId, String topic);

	MqttParam addSinglePublishTopic(int paramId, String topic);

	MqttParam removeSubscribeTopic(int paramId, String topic);

	MqttParam removePublishTopic(int paramId, String topic);

}
