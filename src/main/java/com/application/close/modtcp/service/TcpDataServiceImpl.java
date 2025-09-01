package com.application.close.modtcp.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.application.close.modtcp.entity.TcpData;
import com.application.close.modtcp.payload.TcpPayload;
import com.application.close.modtcp.repo.TcpDataRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TcpDataServiceImpl implements TcpDataService{
	
	private final TcpDataRepo tcpRepo ;
	
	
	@Override
	public TcpData create(TcpPayload tcpPayload) {
		TcpData tcpData = TcpData.of(tcpPayload);
		return tcpRepo.save(tcpData);
	}

	@Override
	public TcpData update(int tcpId, TcpPayload tcpPayload) {
		
		return null;
	}

	@Override
	public void delete(int tcpId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TcpData getById(int tcpId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TcpData> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
