package com.application.close.modtcp.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.close.modtcp.entity.ModTcpData;

public interface TcpDataRepo extends JpaRepository<ModTcpData, Integer> {

}
