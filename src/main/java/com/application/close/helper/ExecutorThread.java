package com.application.close.helper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.application.close.exception.BadRequestException;
import com.application.close.exception.ModbusOperationException;
import com.application.close.links.entity.BridgeExecutor;
import com.application.close.links.service.BridgeExecutorService;
import com.application.close.links.service.ModMqttLinksService;
import com.application.close.modtcp.service.ModService;
import com.application.close.mqtt.service.MqttParamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Service
public class ExecutorThread {

	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	private final ObjectMapper mapper = new ObjectMapper();

	private final BridgeExecutorService executorService;

	private final ModMqttLinksService linksService;

	private final MqttParamService paramService;

	private final ModService modService;

	private final MemoryBuffer buffer;

	@Scheduled(fixedRate = 18000)
	public void fetchingModbusData() {
		executor.submit(this::processModbusBridges);
	}

	private void processModbusBridges() {
		List<BridgeExecutor> bridgeList = executorService.getAll();

		if (bridgeList == null || bridgeList.isEmpty()) {
			log.info("No bridge executors found — skipping execution.");
			return;
		}

		for (BridgeExecutor bridge : bridgeList) {
			try {
				int modTcpId = bridge.getTcpDataId();
				int mqttParamId = linksService.getByModTcpId(modTcpId).getMqttParamId();

				ModbusMaster master = buffer.getModbusMaster().get(modTcpId);
				if (master == null) {
					throw new BadRequestException(String.format("No Modbus master found for TCP ID: %s", modTcpId));
				}

				MqttClient client = (mqttParamId != 0) ? buffer.getMqttClient().get(mqttParamId) : null;

				if (!master.isConnected()) {
					throw new BadRequestException(
							String.format("Modbus master not connected for TCP ID: %s", modTcpId));
				}

				if (mqttParamId == 0 || client == null || !client.isConnected()) {
					throw new BadRequestException(
							String.format("MQTT client not connected or missing for Modbus TCP ID: %s", modTcpId));
				}

				int qos = paramService.getById(mqttParamId).getQos();

				log.info("Processing Modbus → MQTT bridge [TCP ID: {}, MQTT ID: {}]", modTcpId, mqttParamId);

				Object[] result = readModbusData(bridge);
				DataParser mqttData = getMqttData(bridge, result);

				// Mqtt Publish Code
				String message = mapper.writeValueAsString(mqttData);
				client.publish(bridge.getPublishTopic(), message.getBytes(), qos, false);

				executorService.updateTimesatamp(bridge.getId());

				log.info("Modbus → MQTT bridge executed successfully [TCP ID: {}, MQTT ID: {}]", modTcpId, mqttParamId);

			} catch (ModbusOperationException ex) {
				log.error("Modbus read failed. TcpDataId: {}, FunctionType: {}, ExecutorName: {}, Reason: {}",
						bridge.getTcpDataId(), bridge.getFunctionType(), bridge.getBridgeName(), ex.getMessage());

			} catch (Exception ex) {
				log.error("Unexpected error. TcpDataId: {}, FunctionType: {}, ExecutorName: {}, message: {}",
						bridge.getTcpDataId(), bridge.getFunctionType(), bridge.getBridgeName(), ex.getMessage(), ex);
			}

		}
	}

	private Object[] readModbusData(BridgeExecutor bridge) {
		Object[] object;
		switch (bridge.getFunctionType()) {
		case READ_COILS:
			boolean[] coils = modService.readCoils(bridge.getTcpDataId(), bridge.getSlaveId(), bridge.getOffset(),
					bridge.getQuantity());
			object = new Object[coils.length];
			for (int i = 0; i < coils.length; i++) {
				object[i] = coils[i];
			}
			return object;

		case READ_DISCRETE_INPUTS:
			boolean[] inputs = modService.readDiscreteInputs(bridge.getTcpDataId(), bridge.getSlaveId(),
					bridge.getOffset(), bridge.getQuantity());
			object = new Object[inputs.length];
			for (int i = 0; i < inputs.length; i++) {
				object[i] = inputs[i];
			}
			return object;

		case READ_HOLDING_REGISTERS:
			int[] holdingRegisters = modService.readHoldingRegisters(bridge.getTcpDataId(), bridge.getSlaveId(),
					bridge.getOffset(), bridge.getQuantity());
			object = new Object[holdingRegisters.length];
			for (int i = 0; i < holdingRegisters.length; i++) {
				object[i] = holdingRegisters[i];
			}
			return object;

		case READ_INPUT_REGISTERS:
			int[] inputsRegisters = modService.readInputRegisters(bridge.getTcpDataId(), bridge.getSlaveId(),
					bridge.getOffset(), bridge.getQuantity());
			object = new Object[inputsRegisters.length];
			for (int i = 0; i < inputsRegisters.length; i++) {
				object[i] = inputsRegisters[i];
			}
			return object;

		default:
			throw new IllegalArgumentException("Unsupported Modbus function type: " + bridge.getFunctionType());
		}

	}

	private DataParser getMqttData(BridgeExecutor bridge, Object[] modData) {
		DataParser parser = new DataParser();
		parser.setTcpDataId(bridge.getTcpDataId());
		parser.setSlaveId(bridge.getSlaveId());
		parser.setQuantity(bridge.getQuantity());
		parser.setOffset(bridge.getOffset());
		parser.setFunctionType(bridge.getFunctionType().toString());
		parser.setReceivedAt(LocalDateTime.now());

		Map<Integer, Object> data = new HashMap<>();

		int baseAddress;
		switch (bridge.getFunctionType()) {
		case READ_COILS:
			baseAddress = 1; // 00001-based
			break;
		case READ_DISCRETE_INPUTS:
			baseAddress = 10001; // 10001-based
			break;
		case READ_HOLDING_REGISTERS:
			baseAddress = 40001; // 40001-based
			break;
		case READ_INPUT_REGISTERS:
			baseAddress = 30001; // 30001-based
			break;
		default:
			throw new IllegalArgumentException("Unsupported Modbus function type: " + bridge.getFunctionType());
		}

		// Convert 0-based offset to Modbus address
		int startAddress = baseAddress + bridge.getOffset();

		// Fill data map
		for (int j = 0; j < modData.length; j++) {
			data.put(startAddress + j, modData[j]);
		}

		parser.setData(data);
		return parser;
	}

	@PreDestroy
	public void shutdownExecutor() {
		executor.shutdown();
	}

}
