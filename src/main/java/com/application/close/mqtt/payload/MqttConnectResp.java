package com.application.close.mqtt.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MqttConnectResp {

	private String clientId;

	private String mqttUrl;

	private boolean isConnected;

}
