package com.application.close.links.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.application.close.exception.BadRequestException;
import com.application.close.exception.ResourceNotFoundException;
import com.application.close.links.entity.BridgeExecutor;
import com.application.close.links.payload.BridgeExecutorPayload;
import com.application.close.links.repo.BridgeExecutorRepo;
import com.application.close.modtcp.service.TcpDataService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BridgeExecutorServiceImpl implements BridgeExecutorService {

	private final BridgeExecutorRepo executorRepo;

	private final ModMqttLinksService linksService;

	private final TcpDataService dataService;

	@Override
	public BridgeExecutor createBridge(BridgeExecutorPayload executerPayload) {

		// Check for Modbus TcpDataId.
		int tcpDataId = executerPayload.getTcpDataId();
		if (!dataService.existById(tcpDataId))
			throw new BadRequestException("Modbus tcp data not found for tcpDataid: " + tcpDataId);

		// Check for MqttParamId.
		int mqttParamId = linksService.getByModTcpId(executerPayload.getTcpDataId()).getMqttParamId();
		if (mqttParamId == 0)
			throw new BadRequestException("Mqtt param not links for tcpDataid: " + tcpDataId);

		if (executorRepo.existsByPublishTopic(executerPayload.getPublishTopic()))
			throw new BadRequestException("Given publish topics already register for tcpDataid: " + tcpDataId);

		BridgeExecutor bridgeExecutor = new BridgeExecutor();
		bridgeExecutor.setBridgeName(executerPayload.getBridgeName());
		bridgeExecutor.setFunctionType(executerPayload.getFunctionType());
		bridgeExecutor.setOffset(executerPayload.getOffset());
		bridgeExecutor.setPublishTopic(executerPayload.getPublishTopic());
		bridgeExecutor.setQuantity(executerPayload.getQuantity());
		bridgeExecutor.setSlaveId(executerPayload.getSlaveId());
		return executorRepo.save(bridgeExecutor);
	}

	@Override
	public BridgeExecutor updateBridge(int executerId, BridgeExecutorPayload executerPayload) {

		BridgeExecutor bridgeExecutor = getById(executerId);

		// Check for Modbus TcpDataId.
		int tcpDataId = executerPayload.getTcpDataId();
		if (!dataService.existById(tcpDataId))
			throw new BadRequestException("Modbus tcp data not found for tcpDataid: " + tcpDataId);

		// Check for MqttParamId.
		int mqttParamId = linksService.getByModTcpId(executerPayload.getTcpDataId()).getMqttParamId();
		if (mqttParamId == 0) {
			throw new BadRequestException("Mqtt param not links for tcpDataid: " + tcpDataId);
		}

		if (executorRepo.existsByPublishTopic(executerPayload.getPublishTopic()))
			throw new BadRequestException("Given publish topics already register for tcpDataid: " + tcpDataId);

		bridgeExecutor.setBridgeName(executerPayload.getBridgeName());
		bridgeExecutor.setFunctionType(executerPayload.getFunctionType());
		bridgeExecutor.setOffset(executerPayload.getOffset());
		bridgeExecutor.setPublishTopic(executerPayload.getPublishTopic());
		bridgeExecutor.setQuantity(executerPayload.getQuantity());
		bridgeExecutor.setSlaveId(executerPayload.getSlaveId());
		return executorRepo.save(bridgeExecutor);

	}

	@Override
	public BridgeExecutor getById(int executerId) {
		return executorRepo.findById(executerId)
				.orElseThrow(() -> new ResourceNotFoundException("Bridge Executor", "ID", executerId));
	}

	@Override
	public List<BridgeExecutor> getByTcpDataId(int tcpDataId) {
		return executorRepo.findByTcpDataId(tcpDataId);
	}

	@Override
	public List<BridgeExecutor> getAll() {
		return executorRepo.findAll();
	}

	@Override
	public void deleteByTcpDataId(int tcpDataId) {
		executorRepo.deleteAllByTcpDataId(tcpDataId);
	}

	@Override
	public void deleteById(int executerId) {
		BridgeExecutor bridgeExecutor = getById(executerId);
		executorRepo.delete(bridgeExecutor);
	}

}
