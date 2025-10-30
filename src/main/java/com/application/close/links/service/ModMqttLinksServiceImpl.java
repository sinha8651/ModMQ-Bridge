package com.application.close.links.service;

import org.springframework.stereotype.Service;

import com.application.close.exception.ResourceNotFoundException;
import com.application.close.links.entity.ModMqttLinks;
import com.application.close.links.repo.ModMqttLinksRepo;
import com.application.close.modtcp.service.TcpDataService;
import com.application.close.mqtt.service.MqttParamService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ModMqttLinksServiceImpl implements ModMqttLinksService {

	private final ModMqttLinksRepo linksRepo;

	private final MqttParamService mqttParamService;

	private final TcpDataService modTcpService;

	@Override
	public ModMqttLinks connectModToMqtt(int tcpDataId, int mqttParamId) {
		if (!mqttParamService.existsById(mqttParamId))
			throw new ResourceNotFoundException("Mqtt Param", "Id", mqttParamId);

		if (!modTcpService.existById(tcpDataId))
			throw new ResourceNotFoundException("TcpData", "Id", tcpDataId);

		ModMqttLinks links = linksRepo.findByTcpDataId(tcpDataId).orElse(new ModMqttLinks());
		links.setTcpDataId(tcpDataId);
		links.setMqttParamId(mqttParamId);
		return linksRepo.save(links);
	}

	@Override
	public ModMqttLinks disconnectModFromMqtt(int tcpDataId) {
		ModMqttLinks links = getByModTcpId(tcpDataId);
		links.setMqttParamId(0);
		return linksRepo.save(links);
	}

	@Override
	public ModMqttLinks getByModTcpId(int tcpDataId) {
		ModMqttLinks links = linksRepo.findByTcpDataId(tcpDataId)
				.orElseThrow(() -> new ResourceNotFoundException("Modbus TCP", "modTcpId", tcpDataId));
		return links;
	}

	@Override
	public void deleteByModTcpId(int tcpDataId) {
		linksRepo.deleteByTcpDataId(tcpDataId);
	}

	@Override
	public void updateByParamId(int paramId) {
		ModMqttLinks links = linksRepo.findByMqttParamId(paramId).orElse(null);
		if (links != null) {
			links.setMqttParamId(0);
			linksRepo.save(links);
		}
	}

}
