package com.application.close.links.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.close.links.entity.ModMqttLinks;

public interface ModMqttLinksRepo extends JpaRepository<ModMqttLinks, Integer> {

	Optional<ModMqttLinks> findByTcpId(Integer tcpId);

	Optional<ModMqttLinks> findByParamId(Integer paramId);

	void deleteByTcpId(int tcpId);
}
