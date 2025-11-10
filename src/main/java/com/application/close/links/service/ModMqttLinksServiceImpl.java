package com.application.close.links.service;

import org.springframework.stereotype.Service;

import com.application.close.exception.ResourceNotFoundException;
import com.application.close.links.entity.ModMqttLinks;
import com.application.close.links.repo.ModMqttLinksRepo;
import com.application.close.modtcp.repo.TcpDataRepo;
import com.application.close.mqtt.repo.MqttParamRepo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ModMqttLinksServiceImpl implements ModMqttLinksService {

	private final ModMqttLinksRepo linksRepo;

	private final MqttParamRepo paramRepo;

	private final TcpDataRepo tcpRepo;

	@Override
	public ModMqttLinks connectModToMqtt(int tcpDataId, int paramId) {
		if (!paramRepo.existsById(paramId))
			throw new ResourceNotFoundException("Mqtt Param", "Id", paramId);

		if (!tcpRepo.existsById(tcpDataId))
			throw new ResourceNotFoundException("TcpData", "Id", tcpDataId);

		ModMqttLinks links = linksRepo.findByTcpId(tcpDataId).orElse(new ModMqttLinks());
		links.setTcpId(tcpDataId);
		links.setParamId(paramId);
		return linksRepo.save(links);
	}

	@Override
	public ModMqttLinks disconnectModFromMqtt(int tcpDataId) {
		ModMqttLinks links = getByModTcpId(tcpDataId);
		links.setParamId(0);
		return linksRepo.save(links);
	}

	@Override
	public ModMqttLinks getByModTcpId(int tcpDataId) {
		ModMqttLinks links = linksRepo.findByTcpId(tcpDataId)
				.orElseThrow(() -> new ResourceNotFoundException("Modbus TCP", "modTcpId", tcpDataId));
		return links;
	}

	@Override
	public void deleteByModTcpId(int tcpDataId) {
		linksRepo.deleteByTcpId(tcpDataId);
	}

	@Override
	public void removeLinksOfParamId(int paramId) {
		ModMqttLinks links = linksRepo.findByParamId(paramId).orElse(null);
		if (links != null) {
			links.setParamId(0);
			linksRepo.save(links);
		}
	}

}
