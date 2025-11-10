package com.application.close.links.service;

import com.application.close.links.entity.ModMqttLinks;

public interface ModMqttLinksService {

	ModMqttLinks connectModToMqtt(int tcpId, int paramId);

	ModMqttLinks disconnectModFromMqtt(int tcpId);

	ModMqttLinks getByModTcpId(int tcpId);

	void removeLinksOfParamId(int paramId);

	void deleteByModTcpId(int tcpId);

}
