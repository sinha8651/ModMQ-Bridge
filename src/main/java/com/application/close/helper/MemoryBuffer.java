package com.application.close.helper;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.stereotype.Service;

import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import lombok.Getter;

@Getter
@Service
public class MemoryBuffer {

	// Maps a unique TCP data ID to its corresponding ModbusMasterTCP instance.
	private ConcurrentHashMap<Integer, ModbusMaster> modbusMaster;

	// Maps a unique Mqtt param ID to its corresponding MqttClient instance.
	private ConcurrentHashMap<Integer, MqttClient> mqttClient;

	// Maps a unique Modbus data id to its corresponding Mqtt param ID.
	private ConcurrentHashMap<Integer, Integer> modMqttBridge;

	public MemoryBuffer() {
		super();
		this.modbusMaster = new ConcurrentHashMap<>();
		this.mqttClient = new ConcurrentHashMap<>();
		this.modMqttBridge = new ConcurrentHashMap<>();
	}

}
