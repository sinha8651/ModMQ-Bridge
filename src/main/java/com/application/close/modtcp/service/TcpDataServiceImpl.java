package com.application.close.modtcp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.application.close.exception.ResourceNotFoundException;
import com.application.close.modtcp.entity.ModTcpData;
import com.application.close.modtcp.payload.TcpPayload;
import com.application.close.modtcp.repo.TcpDataRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TcpDataServiceImpl implements TcpDataService {

	private final TcpDataRepo tcpRepo;

	@Override
	public ModTcpData create(TcpPayload tcpPayload) {
		ModTcpData tcpData = new ModTcpData();
		tcpData.setHost(tcpPayload.getHost());
		tcpData.setKeepAlive(tcpPayload.isKeepAlive());
		tcpData.setPort(tcpPayload.getPort());
		return tcpRepo.save(tcpData);
	}

	@Override
	public ModTcpData update(int tcpId, TcpPayload tcpPayload) {
		ModTcpData tcpData = getById(tcpId);
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
	public ModTcpData getById(int tcpId) {
		ModTcpData tcpData = tcpRepo.findById(tcpId)
				.orElseThrow(() -> new ResourceNotFoundException("TcpData", "Id", tcpId));
		return tcpData;
	}

	@Override
	public List<ModTcpData> getAll() {
		return tcpRepo.findAll();
	}

}
