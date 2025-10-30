package com.application.close.links.service;

import java.util.List;

import com.application.close.links.entity.BridgeExecutor;
import com.application.close.links.payload.BridgeExecutorPayload;

public interface BridgeExecutorService {

	BridgeExecutor createBridge(BridgeExecutorPayload executerPayload);

	BridgeExecutor updateBridge(int executerId, BridgeExecutorPayload executerPayload);
	
	BridgeExecutor getById(int executerId);

	List<BridgeExecutor> getByTcpDataId(int tcpDataId);

	List<BridgeExecutor> getAll();

	void deleteByTcpDataId(int tcpDataId);

	void deleteById(int executerId);

}
