package com.application.close.links.service;

import com.application.close.links.entity.ModMqttLinks;

public interface ModMqttLinksService {

	ModMqttLinks connectModToMqtt(int tcpDataId, int mqttParamId);

	ModMqttLinks disconnectModFromMqtt(int tcpDataId);

	ModMqttLinks getByModTcpId(int tcpDataId);

	void updateByParamId(int paramId);

	void deleteByModTcpId(int tcpDataId);

}
