package com.application.close.modtcp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.application.close.exception.ResourceNotFoundException;
import com.application.close.modtcp.entity.TcpData;
import com.application.close.modtcp.payload.TcpPayload;
import com.application.close.modtcp.repo.TcpDataRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TcpDataServiceImpl implements TcpDataService {

	private final TcpDataRepo tcpRepo;

	@Override
	public TcpData create(TcpPayload tcpPayload) {
		TcpData tcpData = new TcpData();
		tcpData.setHost(tcpPayload.getHost());
		tcpData.setKeepAlive(tcpPayload.isKeepAlive());
		tcpData.setPort(tcpPayload.getPort());
		return tcpRepo.save(tcpData);
	}

	@Override
	public TcpData update(int tcpId, TcpPayload tcpPayload) {
		TcpData tcpData = getById(tcpId);
		tcpData.setHost(tcpPayload.getHost());
		tcpData.setKeepAlive(tcpPayload.isKeepAlive());
		tcpData.setPort(tcpPayload.getPort());
		return tcpRepo.save(tcpData);
	}

	@Override
	public void delete(int tcpId) {
		tcpRepo.deleteById(tcpId);
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
