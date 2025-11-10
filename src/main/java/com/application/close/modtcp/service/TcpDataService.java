package com.application.close.modtcp.service;

import java.util.List;

import com.application.close.modtcp.entity.TcpData;
import com.application.close.modtcp.payload.TcpPayload;

public interface TcpDataService {

	TcpData create(TcpPayload tcpPayload);

	TcpData update(int tcpId, TcpPayload tcpPayload);

	void delete(int tcpId);

	TcpData getById(int tcpId);

	List<TcpData> getAll();

}
