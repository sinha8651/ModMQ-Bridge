package com.application.close.modtcp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.close.modtcp.entity.TcpData;

public interface TcpDataRepo extends JpaRepository<TcpData, Integer> {

}
