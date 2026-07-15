package com.application.close.helper;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.stereotype.Service;

import com.application.close.links.entity.BridgeExecutor;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import lombok.Getter;

@Getter
@Service
public class MemoryBuffer {

	// Maps a unique TCP data ID to its corresponding ModbusMasterTCP instance.
	private ConcurrentHashMap<Integer, ModbusMaster> modbusMaster;

	// Maps a unique Mqtt param ID to its corresponding MqttClient instance.
	private ConcurrentHashMap<Integer, MqttClient> mqttClient;

	// Maps a unique Bridge Executor ID to its corresponding Bridge Executor
	// instance.
	private ConcurrentHashMap<Integer, BridgeExecutor> bridges;

	public MemoryBuffer() {
		super();
		this.modbusMaster = new ConcurrentHashMap<>();
		this.mqttClient = new ConcurrentHashMap<>();
		this.bridges = new ConcurrentHashMap<>();

	}

}
