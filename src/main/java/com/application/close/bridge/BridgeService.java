package com.application.close.bridge;

public interface BridgeService {

	void connectModToMqtt(int modTcpId, int mqttParamId);

	void disconnectModFromMqtt(int modTcpId);

}
