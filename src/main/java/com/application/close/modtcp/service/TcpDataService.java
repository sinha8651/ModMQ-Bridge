package com.application.close.modtcp.service;

import java.util.List;

import com.application.close.modtcp.entity.ModTcpData;
import com.application.close.modtcp.payload.TcpPayload;

public interface TcpDataService {

	ModTcpData create(TcpPayload tcpPayload);

	ModTcpData update(int tcpId, TcpPayload tcpPayload);

	void delete(int tcpId);

	ModTcpData getById(int tcpId);

	List<ModTcpData> getAll();
}
