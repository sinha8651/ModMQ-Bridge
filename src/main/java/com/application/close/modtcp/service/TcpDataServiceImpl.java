package com.application.close.modtcp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.application.close.exception.BadRequestException;
import com.application.close.exception.ResourceNotFoundException;
import com.application.close.helper.MemoryBuffer;
import com.application.close.links.repo.BridgeExecutorRepo;
import com.application.close.links.service.ModMqttLinksService;
import com.application.close.modtcp.entity.TcpData;
import com.application.close.modtcp.payload.TcpPayload;
import com.application.close.modtcp.repo.TcpDataRepo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TcpDataServiceImpl implements TcpDataService {

	private final TcpDataRepo tcpRepo;

	private final MemoryBuffer buffer;

	private final BridgeExecutorRepo executorRepo;

	private final ModMqttLinksService linksService;

	@Override
	public TcpData create(TcpPayload tcpPayload) {

		if (tcpRepo.existsByHostAndPort(tcpPayload.getHost(), tcpPayload.getPort())) {
			throw new BadRequestException("Host and port already assigned.");
		}

		TcpData tcpData = new TcpData();
		tcpData.setHost(tcpPayload.getHost());
		tcpData.setKeepAlive(tcpPayload.isKeepAlive());
		tcpData.setPort(tcpPayload.getPort());
		return tcpRepo.save(tcpData);
	}

	@Override
	public TcpData update(int tcpId, TcpPayload tcpPayload) {

		TcpData tcpData = getById(tcpId);

		if (buffer.getModbusMaster().containsKey(tcpId) && buffer.getModbusMaster().get(tcpId).isConnected()) {
			throw new BadRequestException("Modbus master connected for tcpDataId: " + tcpId);
		}

		if (tcpRepo.existsByHostAndPort(tcpPayload.getHost(), tcpPayload.getPort())) {
			throw new BadRequestException("Host and port already assigned.");
		}

		buffer.getModbusMaster().remove(tcpId);

		tcpData.setHost(tcpPayload.getHost());
		tcpData.setKeepAlive(tcpPayload.isKeepAlive());
		tcpData.setPort(tcpPayload.getPort());

		return tcpRepo.save(tcpData);
	}

	@Override
	public void delete(int tcpId) {
		TcpData tcpData = getById(tcpId);

		// Remove Modbus Master From Buffer
		if (buffer.getModbusMaster().containsKey(tcpId) && buffer.getModbusMaster().get(tcpId).isConnected()) {
			throw new BadRequestException("Modbus master connected for tcpDataId: " + tcpId);
		}

		buffer.getModbusMaster().remove(tcpId);

		// Remove from executor thread.
		executorRepo.deleteAllBytcpId(tcpId);

		// Removes links .
		linksService.deleteByModTcpId(tcpId);

		tcpRepo.delete(tcpData);
	}

	@Override
	public TcpData getById(int tcpId) {
		TcpData tcpData = tcpRepo.findById(tcpId)
				.orElseThrow(() -> new ResourceNotFoundException("TcpData", "Id", tcpId));
		return tcpData;
	}

	@Override
	public List<TcpData> getAll() {
		return tcpRepo.findAll();
	}

}
