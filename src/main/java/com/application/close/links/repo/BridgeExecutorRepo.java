package com.application.close.links.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.close.links.entity.BridgeExecutor;

public interface BridgeExecutorRepo extends JpaRepository<BridgeExecutor, Integer> {

	List<BridgeExecutor> findBytcpId(int tcpId);

	void deleteAllBytcpId(int tcpId);

	boolean existsByPublishTopic(String publishTopics);

}
