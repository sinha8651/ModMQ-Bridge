package com.application.close.bridge;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.application.close.exception.BadRequestException;
import com.application.close.exception.ResourceNotFoundException;
import com.application.close.helper.MemoryBuffer;
import com.application.close.modtcp.service.TcpDataService;
import com.application.close.mqtt.service.MqttParamService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class BridgeServiceImpl implements BridgeService {

	private final MemoryBuffer buffer;

	private final MqttParamService mqttParamService;

	private final TcpDataService modTcpService;

	@Override
	public void connectModToMqtt(int modTcpId, int mqttParamId) {

		if (!mqttParamService.existsById(mqttParamId))
			throw new ResourceNotFoundException("Mqtt Param", "Id", mqttParamId);

		if (!modTcpService.existById(modTcpId))
			throw new ResourceNotFoundException("TcpData", "Id", modTcpId);

		Map<Integer, Integer> modMqttBridge = buffer.getModMqttBridge();

		if (modMqttBridge.containsKey(modTcpId)) {
			int existingMqttId = modMqttBridge.get(modTcpId);
			throw new BadRequestException(
					"Modbus TCP ID " + modTcpId + " is already linked with MQTT Param ID " + existingMqttId);
		}

		modMqttBridge.put(modTcpId, mqttParamId);

		log.info("Linked Modbus TCP (ID: {}) with MQTT Param (ID: {})", modTcpId, mqttParamId);
	}

	@Override
	public void disconnectModFromMqtt(int modTcpId) {

		Map<Integer, Integer> modMqttBridge = buffer.getModMqttBridge();

		if (!modMqttBridge.containsKey(modTcpId)) {
			throw new ResourceNotFoundException("Modbus TCP", "modTcpId", modTcpId);
		}

		int removedMqttParamId = modMqttBridge.remove(modTcpId);

		log.info("Unlinked Modbus TCP (ID: {}) from MQTT Param (ID: {})", modTcpId, removedMqttParamId);
	}

}
