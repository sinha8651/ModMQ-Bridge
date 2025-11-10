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
import com.application.close.exception.ResourceNotFoundException;
import com.application.close.links.entity.BridgeExecutor;
import com.application.close.links.entity.ModMqttLinks;
import com.application.close.links.repo.ModMqttLinksRepo;
import com.application.close.links.service.BridgeExecutorService;
import com.application.close.modtcp.service.ModService;
import com.application.close.mqtt.entity.MqttParam;
import com.application.close.mqtt.repo.MqttParamRepo;
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

	private final ModService modService;

	private final ModMqttLinksRepo linksRepo;

	private final MqttParamRepo paramRepo;

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
				int tcpId = bridge.getTcpId();
				ModMqttLinks links = linksRepo.findByTcpId(tcpId)
						.orElseThrow(() -> new ResourceNotFoundException("Modbus TCP", "tcpId", tcpId));
				int paramId = links.getParamId();

				ModbusMaster master = buffer.getModbusMaster().get(tcpId);
				if (master == null) {
					throw new BadRequestException(String.format("No Modbus master found for TCP ID: %s", tcpId));
				}

				MqttClient client = (paramId != 0) ? buffer.getMqttClient().get(paramId) : null;

				if (!master.isConnected()) {
					throw new BadRequestException(String.format("Modbus master not connected for TCP ID: %s", tcpId));
				}

				if (paramId == 0 || client == null || !client.isConnected()) {
					throw new BadRequestException(
							String.format("MQTT client not connected or missing for Modbus TCP ID: %s", tcpId));
				}

				int qos = paramRepo.findById(paramId).map(MqttParam::getQos).orElse((byte) 0);

				log.info("Processing Modbus → MQTT bridge [TCP ID: {}, MQTT ID: {}]", tcpId, paramId);

				Object[] result = readModbusData(bridge);
				DataParser mqttData = getMqttData(bridge, result);

				// Mqtt Publish Code
				String message = mapper.writeValueAsString(mqttData);
				client.publish(bridge.getPublishTopic(), message.getBytes(), qos, false);

				executorService.updateTimesatamp(bridge.getId());

				log.info("Modbus → MQTT bridge executed successfully [TCP ID: {}, MQTT ID: {}]", tcpId, paramId);

			} catch (ModbusOperationException ex) {
				log.error("Modbus read failed. TcpDataId: {}, FunctionType: {}, ExecutorName: {}, Reason: {}",
						bridge.getTcpId(), bridge.getFunctionType(), bridge.getBridgeName(), ex.getMessage());

			} catch (Exception ex) {
				log.error("Unexpected error. TcpDataId: {}, FunctionType: {}, ExecutorName: {}, message: {}",
						bridge.getTcpId(), bridge.getFunctionType(), bridge.getBridgeName(), ex.getMessage(), ex);
			}

		}
	}

	private Object[] readModbusData(BridgeExecutor bridge) {
		Object[] object;
		switch (bridge.getFunctionType()) {
		case READ_COILS:
			boolean[] coils = modService.readCoils(bridge.getTcpId(), bridge.getSlaveId(), bridge.getOffset(),
					bridge.getQuantity());
			object = new Object[coils.length];
			for (int i = 0; i < coils.length; i++) {
				object[i] = coils[i];
			}
			return object;

		case READ_DISCRETE_INPUTS:
			boolean[] inputs = modService.readDiscreteInputs(bridge.getTcpId(), bridge.getSlaveId(),
					bridge.getOffset(), bridge.getQuantity());
			object = new Object[inputs.length];
			for (int i = 0; i < inputs.length; i++) {
				object[i] = inputs[i];
			}
			return object;

		case READ_HOLDING_REGISTERS:
			int[] holdingRegisters = modService.readHoldingRegisters(bridge.getTcpId(), bridge.getSlaveId(),
					bridge.getOffset(), bridge.getQuantity());
			object = new Object[holdingRegisters.length];
			for (int i = 0; i < holdingRegisters.length; i++) {
				object[i] = holdingRegisters[i];
			}
			return object;

		case READ_INPUT_REGISTERS:
			int[] inputsRegisters = modService.readInputRegisters(bridge.getTcpId(), bridge.getSlaveId(),
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
		parser.setTcpId(bridge.getTcpId());
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
