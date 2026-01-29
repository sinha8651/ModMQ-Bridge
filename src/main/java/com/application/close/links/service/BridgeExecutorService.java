package com.application.close.links.service;

import java.util.List;

import com.application.close.links.entity.BridgeExecutor;
import com.application.close.links.payload.BridgeExecutorPayload;

public interface BridgeExecutorService {

	BridgeExecutor createBridge(BridgeExecutorPayload executerPayload);

	BridgeExecutor updateBridge(int executerId, BridgeExecutorPayload executerPayload);

	void updateTimesatamp(int executerId);

	BridgeExecutor getById(int executerId);

	List<BridgeExecutor> getByTcpDataId(int tcpDataId);

	List<BridgeExecutor> getAll();

	List<Integer> getAllTcpId();

	void deleteById(int executerId);

}
